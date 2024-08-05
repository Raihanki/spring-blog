package com.raihanhori.spring_blog.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Autowired
	private TokenAuthenticationFilter tokenAuthenticationFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());
		
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.authorizeHttpRequests(req -> 
			req.requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
			.requestMatchers(HttpMethod.POST, "/api/v1/posts").authenticated()
			.requestMatchers(HttpMethod.PUT, "/api/v1/posts/*").authenticated()
			.requestMatchers(HttpMethod.DELETE, "/api/v1/posts/*").authenticated()
			
			.anyRequest().permitAll()
		);
		
		http.authenticationProvider(authenticationProvider);
		
		http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
}
