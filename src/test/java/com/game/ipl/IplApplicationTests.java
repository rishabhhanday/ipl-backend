package com.game.ipl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;


class IplApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(LocalDateTime.now());

	}

	@Test
	void sometest(){
		System.out.println(Instant.now().toString());
	}
}
