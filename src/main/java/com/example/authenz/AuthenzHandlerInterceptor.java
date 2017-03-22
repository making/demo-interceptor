package com.example.authenz;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthenzHandlerInterceptor extends HandlerInterceptorAdapter {
	private final Authorizer authorizer;

	public AuthenzHandlerInterceptor(Authorizer authorizer) {
		this.authorizer = authorizer;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			Authenz authenz = ((HandlerMethod) handler)
					.getMethodAnnotation(Authenz.class);
			if (authenz != null) {
				String token = request.getHeader("X-Athenz-Principal-Auth");
				if (!authorizer.access(authenz.action(), authenz.resource(), token)) {
					throw new AuthenzException("Athenz Authorization Rejected resource="
							+ authenz.resource() + ", action=" + authenz.action());
				}
			}
		}
		return true;
	}
}
