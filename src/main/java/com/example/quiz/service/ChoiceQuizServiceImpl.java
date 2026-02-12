package com.example.quiz.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quiz.entity.ChoiceQuiz;
import com.example.quiz.repository.ChoiceQuizRepository;

@Service
@Transactional
public class ChoiceQuizServiceImpl implements ChoiceQuizService {
	
	@Autowired
	ChoiceQuizRepository repository;
	
	@Override
	public Iterable<ChoiceQuiz> selectAll() {
		return repository.findAll();
	}
	
	@Override
	public void insertQuiz(ChoiceQuiz quiz) {
		repository.save(quiz);
	}
	
	@Override
	public void deleteQuizById(Integer id) {
		repository.deleteById(id);
	}
    
    @Override
    public Optional<ChoiceQuiz> selectOneById(Integer id) {
        return repository.findById(id);
    }
    
    @Override
    public void updateQuiz(ChoiceQuiz quiz) {
    	repository.save(quiz);
    }
    
    @Override
    public Optional<ChoiceQuiz> selectOneRandomQuiz() {
    	Integer randId = repository.getRandomId();
    	if (randId == null) return Optional.empty();
    	return repository.findById(randId);
    }
    
    @Override
    public Boolean checkQuiz(Integer id, Integer myAnswer) {
    	return repository.findById(id)
    			.map(q -> q.getCorrectChoice().equals(myAnswer))
    			.orElse(false);
    }
    
    @Override
    public List<ChoiceQuiz> selectRandomQuizList(int limit) {
    	List<ChoiceQuiz> list = (List<ChoiceQuiz>) repository.findAll();
    	Collections.shuffle(list);
    	return list.stream().limit(limit).toList();
    }
}
