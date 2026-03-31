package com.nutriscan.mpv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NutriScanMvpApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutriScanMvpApplication.class, args);
	}

}
