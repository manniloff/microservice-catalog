package com.microservices.service.catalog.controller;

import com.microservices.service.catalog.model.Catalog;
import com.microservices.service.catalog.model.Movie;
import com.microservices.service.catalog.model.Rating;
import com.microservices.service.catalog.model.UserRating;
import com.microservices.service.catalog.service.MovieInfoService;
import com.microservices.service.catalog.service.UserRatingService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final RestTemplate restTemplate;
    private final UserRatingService userRatingService;
    private final MovieInfoService movieInfoService;

    @GetMapping(value = {"", "/"}, produces = "application/json")
    public List<Catalog> getAll() {
        UserRating ratings = userRatingService.getUserRating();
        return ratings.getRatingList().stream()
                .map(movieInfoService::getCatalogItem)
                .collect(Collectors.toList());
    }

    @HystrixCommand(fallbackMethod = "getFallBackCatalogItem")
    private Catalog getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/info/movie/" + rating.getMovieId(), Movie.class);
        return new Catalog(movie.getName(), movie.getDescription(), rating.getRating());
    }


    private Catalog getFallBackCatalogItem(@PathVariable String userId) {
        return new Catalog("movie.getName()", "movie.getDescription()", 0);
    }
}