package com.dewen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProjectnameApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectnameApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(ProjectnameApplication.class, args);
    }


    @GetMapping("testWriteLog")
    public void testWriteLog() {
        LOGGER.info("feature" + "\t" + "name" + "\t" + "password" + "\t" + "IP" + "\t" + "agent");
    }
}
