package com.akatsuki.block_not;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BlockNotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockNotApplication.class, args);
	}

}
