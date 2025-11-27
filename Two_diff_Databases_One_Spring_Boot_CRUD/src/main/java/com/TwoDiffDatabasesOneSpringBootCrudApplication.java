package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwoDiffDatabasesOneSpringBootCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwoDiffDatabasesOneSpringBootCrudApplication.class, args);
		
		System.out.println("Running on Port: 8080 ...");
	}

}
