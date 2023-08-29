package com.project.javaproject.security;

import java.io.PrintWriter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.project.javaproject.services.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private LoginService loginService = new LoginService();

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		System.out.println("Inside the Pre Handle method");
		String token = loginService.getToken(request);

		if (token == null) {
			PrintWriter out = response.getWriter();
			response.setStatus(403);
			out.println("Missing bearer token");
			return false;
		}

		if (!token.startsWith("Bearer ")) {
			PrintWriter out = response.getWriter();
			response.setStatus(403);
			out.println("The token must be provide like: Bearer {token}");
			return false;
		}

		String tokenError = loginService.validateToken(token.split(" ")[1]);

		if (tokenError != null) {
			PrintWriter out = response.getWriter();
			response.setStatus(403);
			out.println(tokenError);
			return false;
		}

		return true;
	}
}
