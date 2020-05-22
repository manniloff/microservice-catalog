package com.microservices.service.catalog.controller;

import com.microservices.service.catalog.model.Catalog;
import com.microservices.service.catalog.model.Movie;
import com.microservices.service.catalog.model.Rating;
import com.microservices.service.catalog.model.UserRating;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    @GetMapping(value = {"/{userId}"}, produces = "application/json")
    public List<Catalog> getAll(@PathVariable String userId) {

        UserRating ratings = restTemplate.getForObject("http://localhost:8803/rating/user/"+userId, UserRating.class);
        List<Catalog> catalogList = ratings.getRatingList().stream().map(rating -> {
            //Movie movie = restTemplate.getForObject("http://localhost:8802/info/" + rating.getMovieId(), Movie.class);
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8802/info/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();

            return new Catalog(movie.getName(), "Test", rating.getRating());
        })
                .collect(Collectors.toList());

        return catalogList;
    }
}
