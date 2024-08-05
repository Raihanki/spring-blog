package com.raihanhori.spring_blog.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.raihanhori.spring_blog.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
