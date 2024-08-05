package com.raihanhori.spring_blog.user.service;

import com.raihanhori.spring_blog.user.resource.UserResource;

public interface UserService {
	
	UserResource getAuthenticated();
	
}
