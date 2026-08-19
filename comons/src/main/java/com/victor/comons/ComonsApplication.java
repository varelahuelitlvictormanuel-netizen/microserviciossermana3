package com.victor.comons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ComonsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComonsApplication.class, args);
	}

}
