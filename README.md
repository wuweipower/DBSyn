# 架构思想

## 设计原理：

软件分为两部分：① MySQL端，作为生产者端；②PostgreSQL端，我们称之为消费者端。

MySQL端监听BinLog的变化，如创建表，删除表，改变表，增添数据，删除数据，改变数据等。如发生变化，BinLog将被解析为标准SQL语句或者PostgreSQL能执行的SQL语句。com.github.shyiko.mysql.binlog第三方库作为解析BinLog的工具，它可以监听BinLog，并且可以解析mysql数据改变事件的类型，以及提供获得变化数据的API。获得解析后的SQL语句后，将使用RabbitMQ发送到消息队列中。

PostgreSQL端消费这个消息队列中的SQL语句，将SQL语句使用Redis缓冲起来。然后每隔一段时间，将缓存的所有SQL语句按照一定的batch批量执行。并且删除已经执行的sql语句。**在MySQL分表分库的情况下，可以多开几个队列和线程，提高吞吐量。**

### RabbitMQ：

1. 解耦

MySQL端不直接与PostgresSQL端交互，便于单独设计，可以避免管理TCP连接和减少为了保证PG端必须能响应的所作的工作。MySQL端只需要生产数据，将数据发送到RabbitMQ，那么其他端可只与RabbitMQ交互，消费数据。MySQL端不用特别地去修改代码来和特定的PG端连接；并且如果PG端暂时不消费，只需要取消对消息队列的订阅，而不必手动去处理MySQL端因为PG端断开后修改代码的情况。此外，RabbitMQ还可以作集群，在分布式的情况下，可以减低每台服务器的压力。

2. 异步处理

如果MySQL端直接通过HTTP发送过来，让PG端直接执行SQL，为了保证两边同步，需要保证在PG端执行SQL成功后，反馈给MySQL端，随后MySQL端再继续发送下一个，整个过程将比直接发送至RabbitMQ更耗时。

3. 流量削峰

若在短时间内发送大量的SQL语句至PG端，有可能造成PG崩溃（出于机器的性能不佳等原因）。我们可以慢慢消费，这样就可以达到PG端的可用性。从并发的角度考虑崩溃问题：MySQL可能使用了并发处理，使处理的SQL数量比PG单线程多。而PG不对一个队列进行并发的原因是MySQL端对并发做了控制，保证了一致性，但是发送的SQL如果PG也并发处理，可能顺序就会出错，因为不知道哪些SQL语句是MySQL哪个线程处理的，这样就不能保证两个数据库的一致性。

### Redis：

使用的话会自动持久化。万一PG端出现宕机或者重启程序，就会有一个保存文件RDB或者AOF，程序就可以继续执行没有执行完的SQL语句。在此我们进行了性能方面的取舍：若使用map和list的组合，速度会比使用Redis快，但是要持久化这个数据结构，需要管理文件的增删，比较麻烦；因此直接使用Redis，牺牲一点性能。虽然作为缓冲速度比较慢。还有就是考虑到容量问题：Redis可以用几十G内存来做缓存，而一般JVM分给map只有几个G的数据。虽然最终测试发现无法达到这个级别，但是假设考虑分布式环境，这个方案有一定合理性。

### 为什么不直接采用HTTP？

程序中，我们并非直接用http发送一条SQL语句然后执行这条语句。我们测试了使用HTTP发送的吞吐量。分别用Apifox与curl工具，测试发送时延。使用apifox测试20000次请求，耗时特别久。

使用curl工具，发送一次http请求不带参数，需要2ms左右。也就是说，不考虑反馈的情况下，每秒只能发送500条sql语句

而我们测试发送一个简单字符串（相当于带了参数），发送一万条简单字符串给RabbitMQ，完成时间是1s，平均每条0.1ms，也就是每秒1w条，比前面两个方法速度都要快很多。当然如果是复杂字符串可能会久一点。不过已经在带参数的情况下，都比http好很多，这是因为rabbitMQ采用的是AMQP协议，AMQP协议（Advanced Message Queuing Protocol），它是一种开放式标准的消息传递协议，允许不同软件系统之间进行通信。AMQP协议还提供了多个特性来支持高效和可靠的消息传递，包括发布/订阅模式、队列、交换机、路由等。

### 为什么不采用合并发送？

采用一条一条分别发送的方案主要是为了保证及时性，快速反应每一次的变化。



# 项目优点

项目优点：

1.可靠性

Redis定期存缓冲，因此可以应对程序突然崩溃的情况。RabbitMQ不仅具有持久化的特点，消息一定会被消费，而且还可以实现分布式管理。

2.性能

我们对性能进行提升后，数据库的写入速度有明显提高。在测试过程中，项目体现出较好的性能。

以下是我们尝试对项目进行性能提升的过程。

2.1 测试数据准备。

2.1.1 表的结构

create table tb6(te0 int, te1 smallint,te2 Numeric(2,1),te3 float(3),te4 time ,te5 real,te6 datetime, te7 char(3), te8 text,te9 DECIMAL(2,1),te10 tinyint,te11 timestamp, te12 varchar(10),te13 BLOB,primary key(te0)); 

有十四种不同的数据类型（我们基本考虑了所有类型，以实现**可用性**），类型te1在[-32768, 32767]范围中随机产生；te2先生成[-99,99]中的随机数再除以10得到合法数字；te3随机生成小数点后有3位数字的小数；te4随机生成合法的时分秒再拼接成正确的格式；te5随机生成一个小数；te6随机生成合法的年月日在拼接成正确的格式；te7随机生成3个字符并拼接成字符串；te8随机生成多位字符并拼成字符串；te9和te2数据类型相同，但写法不同，调用生成te2类型数据即可；te10在[-128, 123]范围中随机产生；te11调用te4和te6的生成数据函数，并用正确的形式拼接；te12随机生产10个以内数量的字符并拼接成字符串；te13随机生成16进制数据。

随机生成“insert”语句。

随机选定一个数据类型进行“update”操作。在上述te1到te14中随机选中一种数据类型并重新随机产生数据进行更新语句。

删除语句。按顺序删除掉数据库表中的所有数据。

2.2 提升数据库的写入速度**（性能瓶颈之一：PG执行速度慢）**

最开始我们使用JDBC提供的batchUpdate来执行sql。10万数据执行20693ms。

然后我们使用一个循环一条一条执行10万条插入数据。执行时间是17501ms。

我们测试在PGadmin测试。PG的客户端中测试10万条插入SQL语句的速度。耗时2154ms。

受到这个结果启发，我们采用多个sql字符串拼接成一个sql语句，一次执行，真正实现批量执行，速度显著提升。最终10万条数据使用JDBC耗时3809ms。

2.3 测试异步执行的sqlTask间隔时间对吞吐量的影响**（调优，间隔时间适当扩大，以便每次执行尽量多语句）**

以前是每秒执行一次task，吞吐量每秒300条，现在是每3秒执行一次，吞吐量3000多条sql/s。最理想的情况下就是一万条数据在一次sqlTask执行的时候，一次性执行完所有sql。

2.4 我们假象MySQL端使用多个队列发送SQL语句，PG端有多个sqlTask，针对不同表，并行处理。**（调优，使用多线程并行，加速执行速度）**

但是没有好的分布式环境。我们只是提出这个想法，因为**需要对mysql进行分库分表的操作**，binlog针对的是一个mysql实例。一个监听也只能监听这个实例。如果一个程序同时监听多个可能这个机器的I/O会影响整个性能。

我们做了一个模拟测试。使用三个队列发送SQL语句，并且保证这些语句之间是隔离的，执行顺序不会影响最终结果。结果是性能提升是原来的三倍，说明方案可行。每三秒执行接近3万条语句，也就是每秒执行一万条，达到**万级别性能。**

当然，我们可以多开几个队列和线程，理论上最终速度提升与线程数量成正比，实际上，这个增速比受到电脑的**逻辑CPU核心数的限制。**

2.5 我们测试redis写入10万条字符串的速度（查看性能瓶颈是不是这里）

总耗时9218ms，说明使用redis一毫秒大概能存10个字符串。

这和RabbitMQ发送的速度特别接近，某种程度上证明redis作为缓冲的可行性。

我们发现这个性能其实已经是万级别了，可以认为性能瓶颈不在这里。

**最终结论**

性能瓶颈主要是mysql端执行大量SQL语句的I/O性能。

# 技术栈

- Java
- SpringBoot
- RabbitMQ
- Mysql
- PostgreSQL
- Maven
- Redis
- 多线程编程







