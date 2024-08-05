package com.raihanhori.spring_blog.post.request;

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
public class GetPostRequest {

	private Integer page;
	
	private Integer limit;
	
	private String search;
	
	private String username; 
	
}
