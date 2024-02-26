package com.example.quizapp.data

import androidx.lifecycle.LiveData
import com.example.quizapp.data.dao.QuestionDao
import com.example.quizapp.data.model.Question

class QuizRepository(private val questionDao: QuestionDao) {
    val allQuestion:LiveData<List<Question>> = questionDao.getRandomQuestions()

    /**
     * @insertAll  suspend function for inserting data.
     */
    suspend fun insertAll(question:List<Question>){
        questionDao.insertQuestions(question)
    }
}