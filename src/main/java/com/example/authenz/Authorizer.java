package com.example.authenz;

public interface Authorizer {
	boolean access(String action, String resource, String token);
}
