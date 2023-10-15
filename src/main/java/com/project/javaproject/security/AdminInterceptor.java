package com.project.javaproject.security;

import java.io.IOException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.project.javaproject.services.LoginService;
import static com.project.javaproject.utils.ApiResponse.responseUnauthorized;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminInterceptor implements HandlerInterceptor {

    private LoginService loginService;

    AdminInterceptor(LoginService loginService) {
        this.loginService = loginService;
    }
    
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("Inside The Admin Interceptor");
        System.out.println(loginService.getUserSession(request).getRoleName());
        
        if (!loginService.hasPermission(loginService.getUserSession(request))) {
            responseUnauthorized(response, "You do not have permission to view this resource");
            return false;
        }

        return true;
    }
}
