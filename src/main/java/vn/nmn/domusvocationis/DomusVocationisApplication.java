package vn.nmn.domusvocationis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//disable security
//@SpringBootApplication(exclude = {
//		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
//})

 @SpringBootApplication
 @EnableAsync
 @EnableScheduling
public class DomusVocationisApplication {

	public static void main(String[] args) {
		SpringApplication.run(DomusVocationisApplication.class, args);
	}

}
