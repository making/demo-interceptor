package com.example.authenz;

public class DefaultTokenAuthorizer implements Authorizer {
	@Override
	public boolean access(String action, String resource, String token) {
		return token != null;
	}
}
