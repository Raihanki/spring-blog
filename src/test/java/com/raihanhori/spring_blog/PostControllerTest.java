package com.raihanhori.spring_blog;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raihanhori.spring_blog.category.entity.Category;
import com.raihanhori.spring_blog.category.repository.CategoryRepository;
import com.raihanhori.spring_blog.helper.ErrorApiResponseHelper;
import com.raihanhori.spring_blog.helper.PaginationApiResponseHelper;
import com.raihanhori.spring_blog.helper.SuccessApiResponseHelper;
import com.raihanhori.spring_blog.post.entity.Post;
import com.raihanhori.spring_blog.post.repository.PostRepository;
import com.raihanhori.spring_blog.post.request.CreatePostRequest;
import com.raihanhori.spring_blog.post.request.UpdatePostRequest;
import com.raihanhori.spring_blog.post.resource.PostResource;
import com.raihanhori.spring_blog.security.JwtUtils;
import com.raihanhori.spring_blog.user.entity.User;
import com.raihanhori.spring_blog.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	private User user;
	
	private String token;
	
	@BeforeEach
	void setUp() {
		postRepository.deleteAll();
		userRepository.deleteAll();
		
		user = new User();
		user.setUsername("nanakura");
		user.setPassword(passwordEncoder.encode("password"));
		userRepository.save(user);
		
		token = jwtUtils.generateToken(user);
	}
	
	@AfterEach
	void tearDown() {
		postRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	void testCreateSuccess() throws Exception {
		CreatePostRequest request = new CreatePostRequest();
		request.setCategory_id(1);
		request.setBody("Hello Raihanhori");
		request.setTitle("Greeting");
		
		mockMvc.perform(
				post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isCreated())
		.andDo(result -> {
			SuccessApiResponseHelper<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Post post = postRepository.findFirstByTitle(request.getTitle()).orElse(null);
			assertNotNull(post);
			assertEquals(request.getTitle(), post.getTitle());
			assertEquals(user.getUsername(), post.getUser().getUsername());
			assertEquals(request.getCategory_id(), post.getCategory().getId());
			assertNotNull(post.getId());
		});
	}
	
	@Test
	void testCreateFailedValidation() throws Exception {
		CreatePostRequest request = new CreatePostRequest();
		request.setCategory_id(1);
		request.setBody("");
		request.setTitle("");
		
		mockMvc.perform(
				post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
			
			Post post = postRepository.findFirstByTitle(request.getTitle()).orElse(null);
			assertNull(post);
		});
	}
	
	@Test
	void testUpdateSuccess() throws Exception {
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		UpdatePostRequest request = new UpdatePostRequest();
		request.setCategory_id(2);
		request.setBody("Hello Raihanhori updated");
		request.setTitle("Greeting Updated");
		
		mockMvc.perform(
				put("/api/v1/posts/" + post.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponseHelper<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Post oldPost = postRepository.findFirstByTitle(post.getTitle()).orElse(null);
			assertNull(oldPost);
			
			Post updatedPost = postRepository.findFirstByTitle(request.getTitle()).orElse(null);
			assertNotNull(updatedPost);
			assertEquals(request.getTitle(), updatedPost.getTitle());
			assertEquals(user.getUsername(), updatedPost.getUser().getUsername());
			assertEquals(request.getCategory_id(), updatedPost.getCategory().getId());
			assertNotNull(updatedPost.getId());
		});
	}
	
	@Test
	void testUpdateFailedValidation() throws Exception {
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		UpdatePostRequest request = new UpdatePostRequest();
		request.setCategory_id(2);
		request.setBody("");
		request.setTitle("");
		
		mockMvc.perform(
				put("/api/v1/posts/" + post.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
			
			Post newPost = postRepository.findFirstByTitle(request.getTitle()).orElse(null);
			assertNull(newPost);
		});
	}
	
	@Test
	void testUpdateFailedNotFound() throws Exception {
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		UpdatePostRequest request = new UpdatePostRequest();
		request.setCategory_id(2);
		request.setBody("tes");
		request.setTitle("tes");
		
		mockMvc.perform(
				put("/api/v1/posts/99999")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isNotFound());
	}
	
	@Test
	void testUpdateFailedForbidden() throws Exception {
		User user2 = new User();
		user2.setUsername("raihanhori");
		user2.setPassword(passwordEncoder.encode("password"));
		userRepository.save(user2);
		
		String token2 = jwtUtils.generateToken(user2);
		
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		UpdatePostRequest request = new UpdatePostRequest();
		request.setCategory_id(2);
		request.setBody("Hello Raihanhori updated");
		request.setTitle("Greeting Updated");
		
		mockMvc.perform(
				put("/api/v1/posts/" + post.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token2)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isForbidden())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
		});
	}
	
	@Test
	void testGetOneSuccess() throws Exception {
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		mockMvc.perform(
				get("/api/v1/posts/" + post.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponseHelper<PostResource> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Post newPost = postRepository.findFirstByTitle(post.getTitle()).orElse(null);
			assertNotNull(newPost);
			assertEquals(post.getTitle(), newPost.getTitle());
			assertEquals(user.getUsername(), newPost.getUser().getUsername());
			assertEquals(post.getCategory().getId(), newPost.getCategory().getId());
			assertNotNull(newPost.getId());
		});
	}
	
	@Test
	void testGetOneFailedNotFound() throws Exception {
		mockMvc.perform(
				get("/api/v1/posts/9999")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isNotFound())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
		});
	}
	
	@Test
	void testGetAll() throws Exception {
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		mockMvc.perform(
				get("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
		.andDo(result -> {
			PaginationApiResponseHelper<List<PostResource>> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			assertEquals(1, response.getData().size());
			assertNotNull(response.getOptions());
		});
	}
	
	@Test
	void testDeleteSuccess() throws Exception {
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		mockMvc.perform(
				delete("/api/v1/posts/" + post.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponseHelper<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Post newPost = postRepository.findFirstByTitle(post.getTitle()).orElse(null);
			assertNull(newPost);
		});
	}
	
	@Test
	void testDeleteFailedNotFound() throws Exception {
		mockMvc.perform(
				delete("/api/v1/posts/9999")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isNotFound());
	}
	
	@Test
	void testDeleteFailedForbidden() throws Exception {
		User user2 = new User();
		user2.setUsername("raihanhori");
		user2.setPassword(passwordEncoder.encode("password"));
		userRepository.save(user2);
		
		String token2 = jwtUtils.generateToken(user2);
		
		Category category = categoryRepository.findById(1).orElse(null);
		assertNotNull(category);
		
		Post post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setBody("Hello Raihanhori");
		post.setTitle("Greeting");
		postRepository.save(post);
		
		mockMvc.perform(
				delete("/api/v1/posts/" + post.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token2)
		).andExpect(status().isForbidden())
		.andDo(result -> {
			ErrorApiResponseHelper response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
		});
	}
	
}
