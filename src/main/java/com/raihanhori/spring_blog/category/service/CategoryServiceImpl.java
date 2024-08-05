package com.raihanhori.spring_blog.category.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.raihanhori.spring_blog.category.entity.Category;
import com.raihanhori.spring_blog.category.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public List<Category> getAll() {
		List<Category> categories = categoryRepository.findAll();
		
		return categories;
	}

	@Override
	public Category getById(Integer id) {
		Category category = categoryRepository.findById(id).orElse(null);
		
		if (Objects.isNull(category)) {
			return null;
		}
		
		return category;
	}
	
	
}
