package com.project.javaproject.security;

import org.springframework.web.servlet.HandlerInterceptor;

import com.project.javaproject.services.LoginService;
import static com.project.javaproject.utils.ApiResponse.responseUnauthorized;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

	private LoginService loginService;

	AuthInterceptor(LoginService loginService) {
		this.loginService = loginService;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		System.out.println("Inside the Pre Handle method");

		String token = loginService.getToken(request);

		if (token == null) {
			responseUnauthorized(response, "Missing bearer token");
			return false;
		}

		if (!token.startsWith("Bearer ")) {
			responseUnauthorized(response, "The token must be provide like: Bearer {token}");
			return false;
		}

		String tokenError = loginService.validateToken(token.split(" ")[1]);

		if (tokenError != null) {
			responseUnauthorized(response, tokenError);
			return false;
		}

		return true;
	}
}
