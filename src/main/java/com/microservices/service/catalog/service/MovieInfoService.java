package com.microservices.service.catalog.service;

import com.microservices.service.catalog.model.Catalog;
import com.microservices.service.catalog.model.Movie;
import com.microservices.service.catalog.model.Rating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MovieInfoService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MovieInfoService.class);
    private final RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallBackCatalogItem", commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value = "8000"),
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value = "5000")
    })
    public Catalog getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/info/movie/" + rating.getMovieId(), Movie.class);
        return new Catalog(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public Catalog getFallBackCatalogItem(Rating rating) {
        return new Catalog("movie.getName()", "movie.getDescription()", 0);
    }
}
