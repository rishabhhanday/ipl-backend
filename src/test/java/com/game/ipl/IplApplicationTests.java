package com.game.ipl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
class IplApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void sometest(){
		System.out.println(Instant.now().toString());
	}
}
