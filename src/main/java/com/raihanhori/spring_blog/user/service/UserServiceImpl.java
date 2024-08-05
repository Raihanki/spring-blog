package com.raihanhori.spring_blog.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.raihanhori.spring_blog.user.entity.User;
import com.raihanhori.spring_blog.user.repository.UserRepository;
import com.raihanhori.spring_blog.user.resource.UserResource;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElse(null);
	}

	@Override
	public UserResource getAuthenticated() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return UserResource.builder()
				.username(user.getUsername())
				.id(user.getId())
				.build();
	}

}
