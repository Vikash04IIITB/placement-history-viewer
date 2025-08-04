package com.vikash.esd_final_project;

import com.vikash.esd_final_project.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableConfigurationProperties(JwtConfig.class)
public class EsdFinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsdFinalProjectApplication.class, args);
	}

}
