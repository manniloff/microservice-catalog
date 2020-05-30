package com.microservices.service.catalog.service;

import com.microservices.service.catalog.model.Rating;
import com.microservices.service.catalog.model.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserRatingService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MovieInfoService.class);
    private final RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallBackUserRating", commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value = "8000"),
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value = "5000")
    })
    public UserRating getUserRating() {
        return restTemplate.getForObject("http://rating-data-service/rating/user", UserRating.class);
    }

    public UserRating getFallBackUserRating() {
        return new UserRating(Collections.singletonList(new Rating(1, "first", 5)));
    }
}
