package com.coding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class OrderServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceApplication.class);

	private final Environment env;

	public OrderServiceApplication(Environment env) {
		this.env = env;
	}

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		String port = env.getProperty("server.port", "8083");
		String swaggerPath = env.getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");
		log.info("Order Service is running successfully!");
		log.info("Access Swagger UI at: http://localhost:{}{}", port, swaggerPath);
		log.info("Access OpenAPI JSON at: http://localhost:{}/v3/api-docs", port);
	}

}
