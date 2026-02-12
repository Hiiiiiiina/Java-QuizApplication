package com.example.quiz.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quiz.entity.Quiz;
import com.example.quiz.form.QuizForm;
import com.example.quiz.service.QuizService;

@Controller
@RequestMapping("/menu/quiz")
public class QuizController {
	@Autowired
	QuizService service;
	
	@ModelAttribute
	public QuizForm setUpForm() {
		QuizForm form = new QuizForm();
		form.setAnswer(true);
		return form;
	}
	
	@GetMapping
	public String showList(QuizForm quizForm, Model model) {
		quizForm.setNewQuiz(true);
		Iterable<Quiz> list = service.selectAll();
		model.addAttribute("list", list);
		model.addAttribute("title", "登録用フォーム");
		return "quiz/crud";
	}
	
	@PostMapping("/insert")
	public String insert(@Validated QuizForm quizForm, BindingResult bindingResult, 
			Model model, RedirectAttributes redirectAttributes) {
		
		Quiz quiz = new Quiz();
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		
		if(!bindingResult.hasErrors()) {
			service.insertQuiz(quiz);
			redirectAttributes.addFlashAttribute("complete", "登録が完了しました");
			return "redirect:/menu/quiz";
		} else {
			return showList(quizForm, model);
		}
	}
	
	@PostMapping("/upload")
	public String uploadCsv(
			@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("msg", "ファイルが選択されていません");
			return "redirect:/menu/quiz";
		}
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

			String line;
			boolean firstLine = true;
			int count = 0;
			
			while ((line = reader.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
					}
				
				String[] data = line.split(",");
				
				Quiz quiz = new Quiz();
				quiz.setQuestion(data[0]);
				quiz.setAnswer(Boolean.parseBoolean(data[1]));
				quiz.setAuthor(data[2]);
				
				service.insertQuiz(quiz);
			}
			
			redirectAttributes.addFlashAttribute("complete", "CSV登録完了しました");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("msg", "CSV読込エラー");
		}
		
		return "redirect:/menu/quiz";
		}
	
	@GetMapping("/{id}")
	public String showUpdate(QuizForm quizForm, @PathVariable Integer id,Model model) {
		Optional<Quiz> quizOpt = service.selectOneById(id);
		Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
		
		if(quizFormOpt.isPresent()) {
			quizForm =quizFormOpt.get();
		}
		
		makeUpdateModel(quizForm, model);
		return "quiz/crud";
	} 
	
	private void makeUpdateModel(QuizForm quizForm, Model model) {
		model.addAttribute("id", quizForm.getId());
		quizForm.setNewQuiz(false);
		model.addAttribute("quizForm", quizForm);
		model.addAttribute("title", "更新用フォーム");
	}
	
	@PostMapping("/update")
	public String update(
			@Validated QuizForm quizForm,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		Quiz quiz = makeQuiz(quizForm);
		
		if(!result.hasErrors()) {
			service.updateQuiz(quiz);
			redirectAttributes.addFlashAttribute("complete", "更新が完了しました");
			return "redirect:/menu/quiz/" + quiz.getId();
		} else {
			makeUpdateModel(quizForm, model);
			return "quiz/crud";
		}
	}
	
	private Quiz makeQuiz(QuizForm quizForm) {
		Quiz quiz = new Quiz();
		quiz.setId(quizForm.getId());
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		return quiz;
	}
	
	private QuizForm makeQuizForm(Quiz quiz) {
		QuizForm form = new QuizForm();
		form.setId(quiz.getId());
		form.setQuestion(quiz.getQuestion());
		form.setAnswer(quiz.getAnswer());
		form.setAuthor(quiz.getAuthor());
		form.setNewQuiz(false);
		return form;
	}
	
	@PostMapping("/delete")
	public String delete(
			@RequestParam("id") String id,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		service.deleteQuizById(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
		return "redirect:/menu/quiz";
	}
	
	@GetMapping("/play")
	public String showQuiz(QuizForm quizForm, Model model) {
		Optional<Quiz> quizOpt = service.selectOneRandomQuiz();
		if(quizOpt.isPresent()) {
			Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
			quizForm = quizFormOpt.get();
		} else {
			model.addAttribute("msg", "問題がありません・・・");
			return "quiz/play";
		}
		
		model.addAttribute("quizForm", quizForm);
		return "quiz/play";
	}
	
	@PostMapping("/check")
	public String checkQuiz(QuizForm quizForm, @RequestParam Boolean answer, Model model) {
		if(service.checkQuiz(quizForm.getId(), answer)) {
			model.addAttribute("msg", "正解です！");
		} else {
			model.addAttribute("msg", "残念、不正解です・・・");
		}
		return "quiz/answer";
	}
	
	@GetMapping("/play/multi")
	public String startMultiPlay(
			HttpSession session,
			Model model) {
		
		List<Quiz> quizList = service.selectRandomQuizList(10);
		
		if (quizList.isEmpty()) {
			model.addAttribute("msg", "問題がありません・・・");
			return "quiz/quiz_multi_play";
		}
		
		session.setAttribute("quizList", quizList);
		session.setAttribute("currentIndex", 0);
		session.setAttribute("correctCount", 0);
		
		model.addAttribute("quiz", quizList.get(0));
		model.addAttribute("current", 1);
		model.addAttribute("total", quizList.size());
		
		return "quiz/quiz_multi_play";
	}
	
	@PostMapping("/play/multi")
	public String answerMultiPlay(
			@RequestParam Boolean answer,
			HttpSession session,
			Model model) {
		
		List<Quiz> quizList = (List<Quiz>) session.getAttribute("quizList");
		Integer currentIndex = (Integer) session.getAttribute("currentIndex");
		Integer correctCount = (Integer) session.getAttribute("correctCount");
		
		Quiz currentQuiz = quizList.get(currentIndex);
		
		if (currentQuiz.getAnswer().equals(answer)) {
			correctCount++;
		}
		
		currentIndex++;
		
		session.setAttribute("currentIndex", currentIndex);
		session.setAttribute("correctCount", correctCount);
		
		if (currentIndex >= quizList.size()) {
			return "redirect:/menu/quiz/play/result";
		}
		
		Quiz nextQuiz = quizList.get(currentIndex);
		
		model.addAttribute("quiz", nextQuiz);
		model.addAttribute("current", currentIndex + 1);
		model.addAttribute("total", quizList.size());
		
		return "quiz/quiz_multi_play";
	}
	
	@GetMapping("/play/result")
	public String showResult(HttpSession session, Model model) {
		
		Integer correctCount = (Integer) session.getAttribute("correctCount");
		List<Quiz> quizList = (List<Quiz>) session.getAttribute("quizList");
		
		model.addAttribute("correctCount", correctCount);
		model.addAttribute("total", quizList.size());
		
		session.invalidate();
		
		return "quiz/quiz_multi_result";
	}
}


