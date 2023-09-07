package com.project.javaproject.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.project.javaproject.services.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
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
			setNotAuthenticatedMessage(response, "Missing bearer token");
			return false;
		}

		if (!token.startsWith("Bearer ")) {
			setNotAuthenticatedMessage(response, "The token must be provide like: Bearer {token}");
			return false;
		}

		String tokenError = loginService.validateToken(token.split(" ")[1]);

		if (tokenError != null) {
			setNotAuthenticatedMessage(response, tokenError);
			return false;
		}

		return true;
	}

	private void setNotAuthenticatedMessage(HttpServletResponse response, String message) throws IOException {
		PrintWriter out = response.getWriter();
		response.setStatus(403);
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("message", message);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("success", false);
		jsonResponse.put("body", body);
		out.print(jsonResponse);
	}
}
