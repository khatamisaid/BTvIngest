package com.b1.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.b1.testing.config.PasswordEncode;

@SpringBootApplication
public class AplikasiTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AplikasiTestingApplication.class, args);
	}

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncode();
    }

}
