package com.raihanhori.spring_blog.post.service;

import org.springframework.data.domain.Page;

import com.raihanhori.spring_blog.post.request.CreatePostRequest;
import com.raihanhori.spring_blog.post.request.GetPostRequest;
import com.raihanhori.spring_blog.post.request.UpdatePostRequest;
import com.raihanhori.spring_blog.post.resource.PostResource;

public interface PostService {
	
	Page<PostResource> getAll(GetPostRequest request);
	
	PostResource get(Integer id);
	
	void store(CreatePostRequest request);
	
	void update(UpdatePostRequest request);
	
	void delete(Integer id);
	
}
