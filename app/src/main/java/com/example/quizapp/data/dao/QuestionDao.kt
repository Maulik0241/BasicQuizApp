package com.example.quizapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.model.Question
import com.example.quizapp.utils.Constant.TABLE_NAME

@Dao
interface QuestionDao  {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Query("SELECT * FROM $TABLE_NAME ORDER BY RANDOM() LIMIT 10")
     fun getRandomQuestions(): LiveData<List<Question>>

}