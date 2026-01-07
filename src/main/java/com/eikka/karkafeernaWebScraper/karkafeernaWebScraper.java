package com.eikka.karkafeernaWebScraper;

import com.eikka.karkafeernaWebScraper.helpers.JSONMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class karkafeernaWebScraper {

	static void main(String[] args) throws IOException {
		SpringApplication.run(karkafeernaWebScraper.class, args);

		JSONMapper jsonMapper = new JSONMapper();
		jsonMapper.createJSONFile();
	}
}
