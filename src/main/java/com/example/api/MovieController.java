package com.example.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenz.Authenz;

@RestController
public class MovieController {

	@GetMapping("/movie")
	@Authenz(resource = "rec.movie", action = "read")
	Movie getMovie() {
		Movie movie = new Movie();
		movie.name = "Slap Shot";
		movie.director = "George Roy Hill";
		return movie;
	}

	static class Movie {
		public String name;
		public String director;
	}
}
