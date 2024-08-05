package com.raihanhori.spring_blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.raihanhori.spring_blog.helper.ErrorApiResponseHelper;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorController {

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorApiResponseHelper> constraintViolation(ConstraintViolationException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorApiResponseHelper.builder()
						.status(HttpStatus.BAD_REQUEST.value())
						.message(exception.getMessage())
						.build());
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorApiResponseHelper> responseStatus(ResponseStatusException exception) {
		return ResponseEntity.status(exception.getStatusCode())
				.body(ErrorApiResponseHelper.builder()
						.status(exception.getStatusCode().value())
						.message(exception.getReason())
						.build());			
	}
	
}
