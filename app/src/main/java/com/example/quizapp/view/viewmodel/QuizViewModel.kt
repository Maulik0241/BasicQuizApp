package com.example.quizapp.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.QuizRepository
import com.example.quizapp.data.QuestionsDatabase
import com.example.quizapp.data.model.Question
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: QuizRepository
    val allQuestions: LiveData<List<Question>>


    var qIndex: MutableLiveData<Int> = MutableLiveData(0)
    var score: MutableLiveData<Int> = MutableLiveData(0)
    var correct: MutableLiveData<Int> = MutableLiveData(0)
    var wrong: MutableLiveData<Int> = MutableLiveData(0)
    var timeLeftMilliSeconds: MutableLiveData<Long> = MutableLiveData(0)
    var updateQueNo: MutableLiveData<Int> = MutableLiveData(1)


    init {
        val dao = QuestionsDatabase.getInstance(application).questionDao()
        repository = QuizRepository(dao)
        allQuestions = repository.allQuestion
        updateQueNo.value = 1
        timeLeftMilliSeconds.value = 30000
        score.value = 0
        wrong.value = 0
        qIndex.value = 0
    }


}