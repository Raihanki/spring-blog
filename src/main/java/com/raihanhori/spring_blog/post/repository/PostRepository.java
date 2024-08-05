package com.raihanhori.spring_blog.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.raihanhori.spring_blog.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer>{

	@Query(
			"SELECT p FROM Post p " + 
			"JOIN p.user u " + 
			"JOIN p.category c " +
			"WHERE (:username IS NULL OR u.username = :username) " + 
			"AND (:search IS NULL OR p.title LIKE %:search%) "
	)
	Page<Post> findAllPost(@Param("username") String username, @Param("search") String search, Pageable pageable);
	
	Optional<Post> findFirstByTitle(String title);
	
}
