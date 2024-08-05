package com.raihanhori.spring_blog.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreatePostRequest {
	
	@NotNull
	private Integer category_id;
	
	@Size(max = 200)
	@NotBlank
	private String title;
	
	@NotBlank
	private String body;
	
}
