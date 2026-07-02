package com.akatsuki.block_not;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "blocknot.scheduling.enabled=false")
class BlockNotApplicationTests {

	@Test
	void contextLoads() {
	}

}
