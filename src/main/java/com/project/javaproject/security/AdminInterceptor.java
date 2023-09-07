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
public class AdminInterceptor implements HandlerInterceptor {

    private LoginService loginService;

    AdminInterceptor(LoginService loginService) {
        this.loginService = loginService;
    }
    
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("Inside The Admin Interceptor");
        System.out.println(loginService.getUserSession(request).getRoleName());
        
        if (!loginService.getUserSession(request).getRoleName().equals("administrator")) {
            setNotAuthenticatedMessage(response, "You do not have permission to view this resource");
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
