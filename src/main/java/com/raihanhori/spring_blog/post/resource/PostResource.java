package com.raihanhori.spring_blog.post.resource;

import java.time.Instant;

import com.raihanhori.spring_blog.category.resource.CategoryResource;
import com.raihanhori.spring_blog.user.resource.UserResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResource {

	private Integer id;
	
	private CategoryResource category;
	
	private UserResource user;
	
	private String title;
	
	private String body;
	
	private Instant createdAt;
	
}
