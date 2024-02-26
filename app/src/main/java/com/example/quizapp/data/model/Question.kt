package com.example.quizapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.quizapp.utils.Constant.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionText: String,
    val optionA:String,
    val optionB:String,
    val optionC:String,
    val optionD:String,
    val correctAnswer:String
)