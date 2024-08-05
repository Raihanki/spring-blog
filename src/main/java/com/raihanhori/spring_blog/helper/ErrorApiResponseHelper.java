package com.raihanhori.spring_blog.helper;

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
public class ErrorApiResponseHelper {
	
	private Integer status;
	
	private String message;
	
}
