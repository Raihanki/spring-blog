package com.raihanhori.spring_blog.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.raihanhori.spring_blog.helper.ValidationHelper;
import com.raihanhori.spring_blog.security.JwtUtils;
import com.raihanhori.spring_blog.user.entity.User;
import com.raihanhori.spring_blog.user.repository.UserRepository;
import com.raihanhori.spring_blog.user.request.LoginRequest;
import com.raihanhori.spring_blog.user.request.RegisterRequest;
import com.raihanhori.spring_blog.user.resource.AuthResource;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private ValidationHelper validationHelper;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public AuthResource login(LoginRequest request) {
		validationHelper.validate(request);
		
		UsernamePasswordAuthenticationToken credentials = 
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		
		try {
			authenticationManager.authenticate(credentials);
		} catch (AuthenticationException exception) { 
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username or passowrd wrong");
		}
		
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
		
		String token = jwtUtils.generateToken(user);
		
		return AuthResource.builder().token(token).build();
	}

	@Override
	public AuthResource register(RegisterRequest request) {
		validationHelper.validate(request);
		
		Optional<User> checkUser = userRepository.findByUsername(request.getUsername());
		if (checkUser.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is already exists");
		}
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		userRepository.save(user);
		
		String token = jwtUtils.generateToken(user);
		
		return AuthResource.builder().token(token).build();
	}

}
