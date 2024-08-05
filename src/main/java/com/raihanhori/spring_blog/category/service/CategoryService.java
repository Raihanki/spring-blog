package com.raihanhori.spring_blog.category.service;

import java.util.List;

import com.raihanhori.spring_blog.category.entity.Category;

public interface CategoryService {

	List<Category> getAll();
	
	Category getById(Integer id);
	
}
