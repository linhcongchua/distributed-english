package com.enthusiasm.common.simple_web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
public class HealthCommandLineRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCommandLineRunner.class);

    private final SimpleWebProperties simpleWebProperties;

    public HealthCommandLineRunner(SimpleWebProperties simpleWebProperties) {
        this.simpleWebProperties = simpleWebProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("HealthCommandLineRunner starting.");
        SimpleWebUtils.start(simpleWebProperties.port(), Executors.newSingleThreadExecutor());
    }
}
