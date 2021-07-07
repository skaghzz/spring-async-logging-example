package com.example.logging.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void testLog() {
        logger.info("start");
        logger.info("end");
    }
}