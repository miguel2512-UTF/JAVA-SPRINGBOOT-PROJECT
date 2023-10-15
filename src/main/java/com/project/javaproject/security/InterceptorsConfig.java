package com.project.javaproject.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.javaproject.services.LoginService;

@Configuration
@EnableWebMvc
public class InterceptorsConfig implements WebMvcConfigurer {
	@Autowired
	LoginService loginService;

	List<String> protectedRoutes = Arrays.asList("/user/**", "/loan/**", "/role/**", "/me/**");
	List<String> adminRoutes = Arrays.asList("/user/**", "/role/**");

    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CorsInterceptor()).order(0).addPathPatterns("/**");
		registry.addInterceptor(new AuthInterceptor(loginService)).order(1).addPathPatterns(protectedRoutes);
		registry.addInterceptor(new AdminInterceptor(loginService)).order(2).addPathPatterns(adminRoutes);
	}
}
