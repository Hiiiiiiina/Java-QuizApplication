package com.example.quiz.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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

import com.example.quiz.entity.ChoiceQuiz;
import com.example.quiz.form.ChoiceQuizForm;
import com.example.quiz.service.ChoiceQuizService;

@Controller
@RequestMapping("/menu/choice")
public class ChoiceQuizController {

	@Autowired
    ChoiceQuizService service;

    @ModelAttribute
    public ChoiceQuizForm setUpForm() {
    	ChoiceQuizForm form = new ChoiceQuizForm();
    	form.setNewQuiz(true);
    	return form;
    }

    @GetMapping
    public String showList(ChoiceQuizForm form, Model model) {
    	form.setNewQuiz(true);
    	Iterable<ChoiceQuiz> list = service.selectAll();
    	model.addAttribute("list", list);
    	model.addAttribute("title", "4択クイズ登録");
    	return "choice/choice_crud";
    }

    @PostMapping("/insert")
    public String insert(
    		@Validated ChoiceQuizForm form,
    		BindingResult result,
    		Model model,
    		RedirectAttributes redirectAttributes) {
    	
    	if (!result.hasErrors()) {
    		service.insertQuiz(makeEntity(form));
    		redirectAttributes.addFlashAttribute("complete", "登録が完了しました");
    		return "redirect:/menu/choice";
    	} else {
    		return showList(form, model);
    	}
    }
    
    @PostMapping("/upload")
    public String uploadCsv(
    		@RequestParam("file") MultipartFile file,
    		RedirectAttributes redirectAttributes) {
    	
    	if (file.isEmpty()) {
    		redirectAttributes.addFlashAttribute("msg", "ファイルが選択されていません");
    		return "redirect:/menu/choice";
    	}
    	
    	try (BufferedReader reader = new BufferedReader(
    			new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
    		
    		String line;
    		boolean firstLine = true;
    		
    		while ((line = reader.readLine()) != null) {
    			
                if (firstLine) {
                	firstLine = false;
                	continue;
                }
                
                String[] data = line.split(",");
                
                ChoiceQuiz quiz = new ChoiceQuiz();
                quiz.setQuestion(data[0]);
                quiz.setChoice1(data[1]);
                quiz.setChoice2(data[2]);
                quiz.setChoice3(data[3]);
                quiz.setChoice4(data[4]);
                quiz.setCorrectChoice(Integer.parseInt(data[5]));
                quiz.setAuthor(data[6]);
                
                service.insertQuiz(quiz);
    		}
    		
    		redirectAttributes.addFlashAttribute("complete", "CSV登録完了しました");
    		
    	} catch (Exception e) {
        	e.printStackTrace();
        	redirectAttributes.addFlashAttribute("msg", "CSV読込エラー");
        }
    	
    	return "redirect:/menu/choice";
    }
    
    @GetMapping("/{id}")
    public String showUpdate(
    		ChoiceQuizForm form,
    		@PathVariable Integer id,
    		Model model) {

    	
    	Optional<ChoiceQuiz> quizOpt = service.selectOneById(id);
    	
    	if (quizOpt.isPresent()) {
    		ChoiceQuiz quiz = quizOpt.get();
    		form = makeForm(quiz);
    	}
    	
    	makeUpdateModel(form, model);
    	return "choice/choice_crud";
    }
    
    private void makeUpdateModel(ChoiceQuizForm form, Model model) {
    	model.addAttribute("id", form.getId());
    	form.setNewQuiz(false);
    	model.addAttribute("choiceQuizForm", form);
    	model.addAttribute("title", "更新用フォーム");
    }
    
    @PostMapping("/update")
    public String update(
    		@Validated ChoiceQuizForm form,
    		BindingResult result,
    		Model model,
    		RedirectAttributes redirectAttributes) {
    	
    	if (result.hasErrors()) {
    		makeUpdateModel(form, model);
    		return "choice/choice_crud";
    	}
    	
    	service.updateQuiz(makeEntity(form));
    	redirectAttributes.addFlashAttribute("complete", "更新が完了しました");
    	return "redirect:/menu/choice/" + form.getId();
    }
    
    @PostMapping("/delete")
    public String delete(
    		@RequestParam Integer id,
    		RedirectAttributes redirectAttributes) {
    	
    	service.deleteQuizById(id);
    	redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
    	return "redirect:/menu/choice";
    }
    
    private ChoiceQuiz makeEntity(ChoiceQuizForm form) {
    	ChoiceQuiz quiz = new ChoiceQuiz();
    	quiz.setId(form.getId());
    	quiz.setQuestion(form.getQuestion());
    	quiz.setChoice1(form.getChoice1());
    	quiz.setChoice2(form.getChoice2());
    	quiz.setChoice3(form.getChoice3());
    	quiz.setChoice4(form.getChoice4());
    	quiz.setCorrectChoice(form.getCorrectChoice());
    	quiz.setAuthor(form.getAuthor());
    	return quiz;
    }
    
    private ChoiceQuizForm makeForm(ChoiceQuiz quiz) {
    	ChoiceQuizForm form = new ChoiceQuizForm();
    	form.setId(quiz.getId());
    	form.setQuestion(quiz.getQuestion());
    	form.setChoice1(quiz.getChoice1());
    	form.setChoice2(quiz.getChoice2());
    	form.setChoice3(quiz.getChoice3());
    	form.setChoice4(quiz.getChoice4());
    	form.setCorrectChoice(quiz.getCorrectChoice());
    	form.setAuthor(quiz.getAuthor());
    	form.setNewQuiz(false);
    	return form;
    }
    
    @GetMapping("/play")
    public String play(Model model) {
    	Optional<ChoiceQuiz> quizOpt = service.selectOneRandomQuiz();
    	
    	if (quizOpt.isEmpty()) {
    		model.addAttribute("msg", "問題がありません・・・");
    		return "choice/choice_play";
    	}
    	
    	model.addAttribute("quiz", quizOpt.get());
    	return "choice/choice_play";
    }
    
    @PostMapping("/check")
    public String check(
    		@RequestParam Integer id,
    		@RequestParam Integer answer,
    		Model model) {
    	boolean result = service.checkQuiz(id, answer);
    	model.addAttribute("msg", result ? "正解です！" : "残念、不正解です・・・");
    	return "choice/choice_answer";
    }
}
