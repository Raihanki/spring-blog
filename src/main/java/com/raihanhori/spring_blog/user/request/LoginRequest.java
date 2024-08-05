package com.raihanhori.spring_blog.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	@NotBlank
	@Size(min = 3, max = 20)
	private String username;
	
	@NotBlank
	private String password;
	
}
