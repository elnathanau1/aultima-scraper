package com.eau.AUSpider.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomService {

    public int getWaitTime() {
        Random random = new Random();
        int milliseconds = random.nextInt(30000);
        return milliseconds;

    }
}
