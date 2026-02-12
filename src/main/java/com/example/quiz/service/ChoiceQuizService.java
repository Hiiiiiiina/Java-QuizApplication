package com.example.quiz.service;

import java.util.List;
import java.util.Optional;

import com.example.quiz.entity.ChoiceQuiz;

public interface ChoiceQuizService {	
	Iterable<ChoiceQuiz> selectAll();
	void insertQuiz(ChoiceQuiz quiz);
	void deleteQuizById(Integer id);
	
	Optional<ChoiceQuiz> selectOneById(Integer id);
	void updateQuiz(ChoiceQuiz quiz);
	
	Optional<ChoiceQuiz> selectOneRandomQuiz();
	Boolean checkQuiz(Integer id, Integer myAnswer);
	List<ChoiceQuiz> selectRandomQuizList(int limit);
}

