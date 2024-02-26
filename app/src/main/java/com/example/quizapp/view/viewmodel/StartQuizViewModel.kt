package com.example.quizapp.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.QuestionsDatabase
import com.example.quizapp.data.QuizRepository
import com.example.quizapp.data.model.Question
import kotlinx.coroutines.launch

class StartQuizViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = QuestionsDatabase.getInstance(application).questionDao()
    private var repository = QuizRepository(dao)

    /**
     * @addAllQuestion for Insert the data into the database
     */
    fun addAllQuestion(question: List<Question>) = viewModelScope.launch {
        repository.insertAll(question)
    }
}