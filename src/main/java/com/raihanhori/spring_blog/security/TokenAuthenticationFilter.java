package com.raihanhori.spring_blog.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raihanhori.spring_blog.helper.ErrorApiResponseHelper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailsService userDetailService;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = jwtUtils.getTokenFromAuthorizationHeader(request);
		
		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			String username = jwtUtils.getUsernameFromToken(token);
			
			UserDetails user = userDetailService.loadUserByUsername(username);
			
			if (Objects.nonNull(user) && SecurityContextHolder.getContext().getAuthentication() == null 
					&& jwtUtils.isTokenValid(token, user)) {
				UsernamePasswordAuthenticationToken authenticate = 
						new UsernamePasswordAuthenticationToken(user, null, null);
				
				SecurityContextHolder.getContext().setAuthentication(authenticate);
			}
		} catch (JwtException exception) {
			response.setStatus(401);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			
			ErrorApiResponseHelper errorResponse = ErrorApiResponseHelper.builder()
					.status(401).message("Unauthorized")
					.build();
			
			objectMapper.writeValue(response.getWriter(), errorResponse);
			return;
		}
		
		filterChain.doFilter(request, response);
	}

}
