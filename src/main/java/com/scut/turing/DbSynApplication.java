package com.scut.turing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DbSynApplication {
	public static void main(String[] args) {
		SpringApplication.run(DbSynApplication.class, args);
	}
}
