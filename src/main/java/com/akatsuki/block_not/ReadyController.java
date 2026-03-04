package com.akatsuki.block_not;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadyController {

	@GetMapping("/")
	public String ready() {
		return "I'm ready";
	}
}
