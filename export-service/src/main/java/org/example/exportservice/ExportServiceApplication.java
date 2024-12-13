package org.example.exportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExportServiceApplication {

	public static void main(String[] args) {
//		SpringApplication.run(ExportServiceApplication.class, args);
		System.exit(SpringApplication.exit(SpringApplication.run(ExportServiceApplication.class, args)));
	}

}
