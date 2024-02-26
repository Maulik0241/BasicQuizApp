package com.example.quizapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quizapp.data.dao.QuestionDao
import com.example.quizapp.data.model.Question
import com.example.quizapp.utils.Constant.DATABASE_NAME

@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: QuestionsDatabase? = null

        fun getInstance(context: Context): QuestionsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionsDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}