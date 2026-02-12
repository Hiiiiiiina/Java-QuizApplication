package com.example.quiz.form;

public class ChoiceQuizForm {
	
	private Integer id;
	private String question;
	
	private String choice1;
	private String choice2;
	private String choice3;
	private String choice4;
	
	private Integer correctChoice;
	
	private String author;
	
	private boolean newQuiz;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
    	this.id = id;
    }
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getChoice1() {
		return choice1;
	}
	
	public void setChoice1(String choice1) {
		this.choice1 = choice1;
	}
	
	public String getChoice2() {
		return choice2;
	}
	
	public void setChoice2(String choice2) {
		this.choice2 = choice2;
	}
	
	public String getChoice3() {
		return choice3;
	}
	
	public void setChoice3(String choice3) {
		this.choice3 = choice3;
	}
	
	public String getChoice4() {
		return choice4;
	}
	
	public void setChoice4(String choice4) {
		this.choice4 = choice4;
	}
	
	public Integer getCorrectChoice() {
		return correctChoice;
	}
	
	public void setCorrectChoice(Integer correctChoice) {
		this.correctChoice = correctChoice;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public boolean isNewQuiz() {
		return newQuiz;
	}
	
	public void setNewQuiz(boolean newQuiz) {
		this.newQuiz = newQuiz;
	}
}
