package com.darshil.quiz_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.darshil.quiz_service.dao.QuestionDao;
import com.darshil.quiz_service.dao.QuizDao;
import com.darshil.quiz_service.model.Quiz;
import com.darshil.quiz_service.model.Response;
import com.darshil.quiz_service.model.Question;
import com.darshil.quiz_service.model.QuestionWrapper;



@Service
public class QuizService {
    @Autowired
    QuizDao quizDao;
    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Question> questions = questionDao.findRandomQuestionByCategory(category, numQ);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id){
        Optional<Quiz> quiz = quizDao.findById(id);

        List<Question> questionsFromDB = quiz.get().getQuestions();
        List<QuestionWrapper> questioForUser = new ArrayList<>();

        for(Question q : questionsFromDB){
            QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());
            questioForUser.add(qw);
        }

        return new ResponseEntity<>(questioForUser, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses){
        Quiz quiz = quizDao.findById(id).get();
        List<Question> questions = quiz.getQuestions();
        int correct = 0;
        int i = 0;
        for(Response response : responses){
            if(response.getResponse().equals(questions.get(i).getRightAnswer()))
            correct++;

            i++;
        }
        return new ResponseEntity<>(correct, HttpStatus.OK);
    }

}
