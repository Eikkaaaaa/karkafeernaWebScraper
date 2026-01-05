package com.example.karkafeernaWebScraper;

import com.example.karkafeernaWebScraper.helpers.JSONMaker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class karkafeernaWebScraper {

	static void main(String[] args) throws IOException {
		SpringApplication.run(karkafeernaWebScraper.class, args);

		JSONMaker jsonMaker = new JSONMaker();
		jsonMaker.createJSONFile();
	}
}
