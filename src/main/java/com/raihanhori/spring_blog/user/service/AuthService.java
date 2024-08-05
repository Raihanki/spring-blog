package com.raihanhori.spring_blog.user.service;

import com.raihanhori.spring_blog.user.request.LoginRequest;
import com.raihanhori.spring_blog.user.request.RegisterRequest;
import com.raihanhori.spring_blog.user.resource.AuthResource;

public interface AuthService {
	
	AuthResource login(LoginRequest request);
	
	AuthResource register(RegisterRequest request);
	
}
