package com.example.quiz.service;

import java.util.List;
import java.util.Optional;

import com.example.quiz.entity.Quiz;

public interface QuizService {
	Iterable<Quiz> selectAll();
	Optional<Quiz> selectOneById(Integer id);
	Optional<Quiz> selectOneRandomQuiz();
	Boolean checkQuiz(Integer id, Boolean myAnswer);
	
	List<Quiz> selectRandom10();
	List<Quiz> selectRandomQuizList(int limit);
	
	void insertQuiz(Quiz quiz);
	void updateQuiz(Quiz quiz);
	void deleteQuizById(Integer id);
}
