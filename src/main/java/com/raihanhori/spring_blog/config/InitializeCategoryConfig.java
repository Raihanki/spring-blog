package com.raihanhori.spring_blog.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.raihanhori.spring_blog.category.entity.Category;
import com.raihanhori.spring_blog.category.repository.CategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class InitializeCategoryConfig implements CommandLineRunner {
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public void run(String... args) throws Exception {
		List<Category> listCategories = categoryRepository.findAll();
		
		if (listCategories.size() < 2) {
			List<Category> categories = new ArrayList<Category>();
			categories.add(new Category(null, "Programming"));
			categories.add(new Category(null, "Sports"));
			categories.add(new Category(null, "Technologies"));
			categories.add(new Category(null, "Music"));
			categories.add(new Category(null, "Education"));
			categories.add(new Category(null, "LifeStyle"));
			categories.add(new Category(null, "Health"));
			categories.add(new Category(null, "Gaming"));
			categories.add(new Category(null, "Art"));
			
			categoryRepository.saveAll(categories);
			
			log.info("category seeded successfully");
		}
		
		log.info("you have more than 2 categories");
	}
	
}
