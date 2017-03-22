package com.example.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenz.Authenz;

@RestController
public class TvShowController {

	@GetMapping("/tvshow")
	@Authenz(resource = "rec.tvshow", action = "read")
	TvShow getTvShow() {
		TvShow tvShow = new TvShow();
		tvShow.name = "Middle";
		tvShow.channel = "ABC";
		return tvShow;
	}

	static class TvShow {
		public String name;
		public String channel;
	}
}
