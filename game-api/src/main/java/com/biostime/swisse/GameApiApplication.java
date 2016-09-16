package com.biostime.swisse;

import com.biostime.swisse.controller.BigWheelController;
import com.biostime.swisse.util.MyAppProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath:META-INF/spring/swisse-spring-mvc.xml","classpath:META-INF/spring/swisse-spring.xml",})
@SpringBootApplication
public class GameApiApplication implements CommandLineRunner, ApplicationRunner {
	private static final Logger LOGGER = LogManager.getLogger(BigWheelController.class);

	public static void main(String[] args) {
		SpringApplication.run(GameApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("---------------------------------------------------------------");
		LOGGER.info("----------------------------CommandLineRunner-------------------");
	}

	@Bean
	public CommandLineRunner runner(){
		return new CommandLineRunner() {
			public void run(String... args){//Run some process here
				LOGGER.info("----\n CommandLineRunner interface as a bean by marking the method\n" +
						"with @Bean annotation");
				LOGGER.info("*************************************************************");
			}
		};
	}

	@Bean
	String info(){
		return "Just a simple String bean";
	}
	@Autowired
	String info;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOGGER.info("## > ApplicationRunner Implementation...");
		LOGGER.info("Accessing the Info bean: " + info);
		args.getNonOptionArgs().forEach(file -> LOGGER.info(file));

	}

	@Value("${myapp.server-ip}")
	String serverIp;

	@Autowired
	MyAppProperties props;

	@Bean
	public CommandLineRunner values(){
		return args -> {
			LOGGER.info(" > The Server IP is: " + serverIp);
			LOGGER.info(" > App Name: " + props.getName());
			LOGGER.info(" > App Info: " + props.getDescription());
		};
	}

}
