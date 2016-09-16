package com.apress.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringBootJournalApplication  extends SpringBootServletInitializer {

	/**
	 * extend from the SpringBootServletInitializer abstract class. This
	 is required because the Spring web is using the Servlet 3.0 support and it’s necessary to bootstrap your
	 application when it’s being deployed by the container.
	 * @param application  bootstrap SpringApplicationBuilder
	 * @return SpringApplicationBuilder
     */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootJournalApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJournalApplication.class, args);
	}
}
