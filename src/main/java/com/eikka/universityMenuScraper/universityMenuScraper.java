package com.eikka.universityMenuScraper;

import com.eikka.universityMenuScraper.helpers.JSONMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class universityMenuScraper {

	static void main(String[] args) throws IOException {
		SpringApplication.run(universityMenuScraper.class, args);

		JSONMapper jsonMapper = new JSONMapper();
		jsonMapper.createJSONFile();
	}
}
