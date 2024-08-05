package com.raihanhori.spring_blog.post.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.raihanhori.spring_blog.category.entity.Category;
import com.raihanhori.spring_blog.category.resource.CategoryResource;
import com.raihanhori.spring_blog.category.service.CategoryService;
import com.raihanhori.spring_blog.helper.ValidationHelper;
import com.raihanhori.spring_blog.post.entity.Post;
import com.raihanhori.spring_blog.post.repository.PostRepository;
import com.raihanhori.spring_blog.post.request.CreatePostRequest;
import com.raihanhori.spring_blog.post.request.GetPostRequest;
import com.raihanhori.spring_blog.post.request.UpdatePostRequest;
import com.raihanhori.spring_blog.post.resource.PostResource;
import com.raihanhori.spring_blog.user.entity.User;
import com.raihanhori.spring_blog.user.resource.UserResource;
import com.raihanhori.spring_blog.user.service.UserService;

@Service
public class PostServiceImpl implements PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ValidationHelper validationHelper;

	@Override
	public Page<PostResource> getAll(GetPostRequest request) {
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getLimit(), Sort.by("createdAt").descending());
		
		Page<Post> posts = postRepository.findAllPost(request.getUsername(), request.getSearch(), pageable);
		List<PostResource> listPost = posts.stream().map(post -> this.toPostResource(post)).toList();
		
		return new PageImpl<PostResource>(listPost);
	}

	@Override
	public PostResource get(Integer id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
		
		return this.toPostResource(post);
	}

	@Override
	public void store(CreatePostRequest request) {
		validationHelper.validate(request);
		
		Category category = categoryService.getById(request.getCategory_id());
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		Post post = new Post();
		post.setTitle(request.getTitle());
		post.setBody(request.getBody());
		post.setCategory(category);
		post.setUser(user);
		postRepository.save(post);
	}

	@Override
	public void update(UpdatePostRequest request) {
		validationHelper.validate(request);
		
		Post post = this.checkPostAuthorize(request.getId());
		Category category = categoryService.getById(request.getCategory_id());
		if (Objects.isNull(category)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found");
		}
		
		post.setTitle(request.getTitle());
		post.setBody(request.getBody());
		post.setCategory(category);
		postRepository.save(post);
	}

	@Override
	public void delete(Integer id) {
		Post post = this.checkPostAuthorize(id);
		
		postRepository.delete(post);
	}
	
	private Post checkPostAuthorize(Integer postId) {
		UserResource user = userService.getAuthenticated();
		
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
		
		if (post.getUser().getId() != user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot modify this post");
		}
		
		return post;
	}
	
	private PostResource toPostResource(Post post) {
		CategoryResource category = CategoryResource.builder()
				.id(post.getCategory().getId())
				.name(post.getCategory().getName())
				.build();
		
		UserResource user = UserResource.builder()
				.id(post.getUser().getId())
				.username(post.getUser().getUsername())
				.build();
		
		return PostResource.builder()
				.id(post.getId())
				.category(category)
				.user(user)
				.title(post.getTitle())
				.body(post.getBody())
				.createdAt(post.getCreatedAt())
				.build();
	}

}
