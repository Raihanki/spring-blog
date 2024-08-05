package com.raihanhori.spring_blog.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationApiResponseHelper<T> {

	private Integer status;
	
	private T data;
	
	private OptionPagination options;
	
}
