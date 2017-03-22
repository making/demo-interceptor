package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.example.authenz.AuthenzHandlerInterceptor;
import com.example.authenz.Authorizer;
import com.example.authenz.DefaultTokenAuthorizer;

@SpringBootApplication
public class DemoInterceptorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoInterceptorApplication.class, args);
	}

	@Configuration
	static class MvcConfig extends WebMvcConfigurerAdapter {
		private final Authorizer authorizer;

		MvcConfig(Authorizer authorizer) {
			this.authorizer = authorizer;
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new AuthenzHandlerInterceptor(authorizer));
		}
	}

	@Bean
	@ConditionalOnMissingBean
	Authorizer defaultTokenAuthorizer() {
		return new DefaultTokenAuthorizer();
	}
}
