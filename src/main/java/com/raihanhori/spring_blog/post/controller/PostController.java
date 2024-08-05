package com.raihanhori.spring_blog.post.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.raihanhori.spring_blog.helper.OptionPagination;
import com.raihanhori.spring_blog.helper.PaginationApiResponseHelper;
import com.raihanhori.spring_blog.helper.SuccessApiResponseHelper;
import com.raihanhori.spring_blog.post.request.CreatePostRequest;
import com.raihanhori.spring_blog.post.request.GetPostRequest;
import com.raihanhori.spring_blog.post.request.UpdatePostRequest;
import com.raihanhori.spring_blog.post.resource.PostResource;
import com.raihanhori.spring_blog.post.service.PostService;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
	
	@Autowired
	private PostService postService;
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public PaginationApiResponseHelper<List<PostResource>> getAll(
			@RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
			@RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "username", required = false) String username
	) {
		GetPostRequest request = new GetPostRequest();
		request.setPage(page);
		request.setLimit(limit);
		request.setSearch(search);
		request.setUsername(username);
		
		Page<PostResource> response = postService.getAll(request);
		
		return PaginationApiResponseHelper.<List<PostResource>>builder()
				.status(200)
				.data(response.getContent())
				.options(
						OptionPagination.builder()
						.current_page(response.getNumber() + 1)
						.total_page(response.getTotalPages())
						.total_data(response.getTotalElements())
						.limit_per_page(response.getSize())
						.username(username)
						.search(search)
						.build()
				)
				.build();
	}
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponseHelper<PostResource> get(@PathVariable(name = "id") Integer id) {
		PostResource response = postService.get(id);
		
		return SuccessApiResponseHelper.<PostResource>builder()
				.status(200)
				.data(response)
				.build();		
	}
	
	@PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public SuccessApiResponseHelper<String> store(@RequestBody CreatePostRequest request) {
		postService.store(request);
		
		return SuccessApiResponseHelper.<String>builder()
				.status(201)
				.data("successfully created")
				.build();		
	}
	
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponseHelper<String> update(@RequestBody UpdatePostRequest request, @PathVariable(name = "id") Integer id) {
		request.setId(id);
		postService.update(request);
		
		return SuccessApiResponseHelper.<String>builder()
				.status(200)
				.data("successfully updated")
				.build();		
	}
	
	@DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponseHelper<String> destroy(@PathVariable(name = "id") Integer id) {
		postService.delete(id);
		
		return SuccessApiResponseHelper.<String>builder()
				.status(200)
				.data("successfully deleted")
				.build();		
	}
	
}
