package com.example.quiz.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.quiz.entity.ChoiceQuiz;

public interface ChoiceQuizRepository
	extends CrudRepository<ChoiceQuiz, Integer> {
	@Query("SELECT id FROM choice_quiz ORDER BY RANDOM() LIMIT 1")
	Integer getRandomId();
}

