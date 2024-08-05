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
public class OptionPagination {
	
	private Integer limit_per_page;
	
	private Integer current_page;
	
	private Integer total_page;
	
	private Long total_data;
	
	private String username;
	
	private String search;
	
}
