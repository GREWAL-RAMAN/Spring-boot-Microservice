package com.grewal.clientauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ClientAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientAuthServiceApplication.class, args);
	}

}
