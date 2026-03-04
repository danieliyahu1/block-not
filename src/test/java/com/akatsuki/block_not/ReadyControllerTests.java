package com.akatsuki.block_not;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReadyControllerTests {

	@Test
	void ready_returnsImReady() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ReadyController()).build();

		mockMvc.perform(get("/ready"))
				.andExpect(status().isOk())
				.andExpect(content().string("I'm ready"));
	}
}
