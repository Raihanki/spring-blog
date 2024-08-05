package com.raihanhori.spring_blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.raihanhori.spring_blog.helper.SuccessApiResponseHelper;
import com.raihanhori.spring_blog.user.request.LoginRequest;
import com.raihanhori.spring_blog.user.request.RegisterRequest;
import com.raihanhori.spring_blog.user.resource.AuthResource;
import com.raihanhori.spring_blog.user.resource.UserResource;
import com.raihanhori.spring_blog.user.service.AuthService;
import com.raihanhori.spring_blog.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthService authService;
	
	@PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponseHelper<AuthResource> login(@RequestBody LoginRequest request) {
		AuthResource response = authService.login(request);
		return SuccessApiResponseHelper.<AuthResource>builder()
					.status(200)
					.data(response)
					.build();
	}
	
	@PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public SuccessApiResponseHelper<AuthResource> register(@RequestBody RegisterRequest request) {
		AuthResource response = authService.register(request);
		return SuccessApiResponseHelper.<AuthResource>builder()
					.status(201)
					.data(response)
					.build();
	}
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponseHelper<UserResource> getUser() {
		UserResource response = userService.getAuthenticated();
		return SuccessApiResponseHelper.<UserResource>builder()
				.status(200)
				.data(response)
				.build();
	}
	
	
}
