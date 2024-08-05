package com.raihanhori.spring_blog;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raihanhori.spring_blog.helper.ErrorApiResponseHelper;
import com.raihanhori.spring_blog.helper.SuccessApiResponseHelper;
import com.raihanhori.spring_blog.security.JwtUtils;
import com.raihanhori.spring_blog.user.entity.User;
import com.raihanhori.spring_blog.user.repository.UserRepository;
import com.raihanhori.spring_blog.user.request.LoginRequest;
import com.raihanhori.spring_blog.user.request.RegisterRequest;
import com.raihanhori.spring_blog.user.resource.AuthResource;
import com.raihanhori.spring_blog.user.resource.UserResource;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}
	
	@Test
	void testRegisterSuccess() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("nanakura");
		request.setPassword("password");
		
		mockMvc.perform(
				post("/api/v1/users/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isCreated())
		.andDo(result -> {
			SuccessApiResponseHelper<AuthResource> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData().getToken());
			
			User user = userRepository.findByUsername(request.getUsername()).orElse(null);
			assertNotNull(user);
			assertEquals(request.getUsername(), user.getUsername());
			assertNotNull(user.getPassword());
			assertNotNull(user.getId());
		});
	}
	
	@Test
	void testRegisterFailedValidation() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("");
		request.setPassword("");
		
		mockMvc.perform(
				post("/api/v1/users/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
			assertNotNull(response.getStatus());
		});
	}
	
	@Test
	void testRegisterFailedUsernameAlreadyExist() throws Exception {
		User user = new User();
		user.setUsername("nanakura");
		user.setPassword(passwordEncoder.encode("password"));
		userRepository.save(user);
		
		RegisterRequest request = new RegisterRequest();
		request.setUsername("nanakura");
		request.setPassword("password");
		
		mockMvc.perform(
				post("/api/v1/users/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
			assertNotNull(response.getStatus());
		});
	}
	
	@Test
	void testLoginSuccess() throws Exception {
		User user = new User();
		user.setUsername("nanakura");
		user.setPassword(passwordEncoder.encode("password"));
		userRepository.save(user);
		
		LoginRequest request = new LoginRequest();
		request.setUsername("nanakura");
		request.setPassword("password");
		
		mockMvc.perform(
				post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponseHelper<AuthResource> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData().getToken());
		});
	}
	
	@Test
	void testLoginFailed() throws Exception {
		LoginRequest request = new LoginRequest();
		request.setUsername("wrong-username");
		request.setPassword("wrong-password");
		
		mockMvc.perform(
				post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
			assertNotNull(response.getStatus());
		});
	}
	
	@Test
	void testGetAuthenticatedUserSuccess() throws Exception {
		User user = new User();
		user.setUsername("nanakura");
		user.setPassword(passwordEncoder.encode("password"));
		userRepository.save(user);
		
		String token = jwtUtils.generateToken(user);
		
		mockMvc.perform(
				get("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponseHelper<UserResource> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData());
			
			assertEquals(user.getUsername(), response.getData().getUsername());
			assertEquals(user.getId(), response.getData().getId());
		});
	}
	
	@Test
	void testGetAuthenticatedUserFailed() throws Exception {
		mockMvc.perform(
				get("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isForbidden());
	}
	
}
