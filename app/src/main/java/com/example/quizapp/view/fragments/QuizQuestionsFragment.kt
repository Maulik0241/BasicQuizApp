package com.example.quizapp.view.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.data.QuestionsDatabase
import com.example.quizapp.data.model.Question
import com.example.quizapp.databinding.FragmentQuizQuestionsBinding
import com.example.quizapp.view.viewmodel.QuizViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class QuizQuestionsFragment : Fragment() {

    private lateinit var binding:FragmentQuizQuestionsBinding
    private lateinit var viewModel: QuizViewModel
    private lateinit var question: List<Question>

    // Timer variables
    private var countDownTimer: CountDownTimer? = null
    private val countDownInMilliSecond: Long = 30000
    private val countDownInterval: Long = 1000
    private var timeLeftMilliSeconds: Long = 0
    private var defaultColor: ColorStateList? = null

    private var qIndex = 0
    private var score = 0
    private var correct = 0
    private var wrong = 0
    private var updateQueNo = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuizQuestionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance( Application())
        )[QuizViewModel::class.java]


        viewModel.allQuestions.observe(requireActivity(), Observer{ questions->
            questions.let {
                question = it
                Log.e(TAG, "onCreate: $it", )
                initViews()
            }
        })
        viewModel.qIndex.observe(requireActivity(), Observer {
            qIndex = it
        })
        viewModel.score.observe(requireActivity(), Observer {
            score = it
        })
        viewModel.correct.observe(requireActivity(), Observer {
            correct = it
        })
        viewModel.wrong.observe(requireActivity(), Observer {
            wrong = it
        })
        viewModel.updateQueNo.observe(requireActivity(), Observer {
            updateQueNo = it
        })

        viewModel.timeLeftMilliSeconds.observe(requireActivity()) {
            timeLeftMilliSeconds = it
        }


    }

    @SuppressLint("SetTextI18n")
    private fun showNextQuestion() {
        resetRadioButtonState()
        Log.e(TAG, "showNextQuestion: $qIndex", )
        binding.apply {
            if(updateQueNo < 10){
                tvNoOfQues.text = "${updateQueNo + 1}/10"
                updateQueNo++
            }
            Log.e(TAG, "showNextQuestion: ${question.size}", )
            if(qIndex < question.size  ){

                timeLeftMilliSeconds = countDownInMilliSecond
                statCountDownTimer()

                tvQuestion.text = question[qIndex].questionText
                rbtn1.text = question[qIndex].optionA
                rbtn2.text = question[qIndex].optionB
                rbtn3.text = question[qIndex].optionC
                rbtn4.text = question[qIndex].optionD
            }else{
                val bundle = Bundle()
                bundle.putInt("score", score)
                bundle.putInt("correct", correct)
                findNavController().navigate(R.id.action_quizQuestionsFragment_to_resultFragment,bundle)
                requireActivity().finish()

            }
            radioGroup.clearCheck()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {

        binding.apply {
            tvQuestion.text = question[qIndex].questionText
            rbtn1.text = question[qIndex].optionA
            rbtn2.text = question[qIndex].optionB
            rbtn3.text = question[qIndex].optionC
            rbtn4.text = question[qIndex].optionD

            // Set individual click listeners for each radio button
            rbtn1.setOnClickListener { onRadioButtonClicked(rbtn1) }
            rbtn2.setOnClickListener { onRadioButtonClicked(rbtn2) }
            rbtn3.setOnClickListener { onRadioButtonClicked(rbtn3) }
            rbtn4.setOnClickListener { onRadioButtonClicked(rbtn4) }

            tvNoOfQues.text = "$updateQueNo/10"
            tvQuestion.text = question[qIndex].questionText

            defaultColor = quizTimer.textColors

            timeLeftMilliSeconds = countDownInMilliSecond

            statCountDownTimer()
        }
    }
    private fun statCountDownTimer(){
        countDownTimer = object : CountDownTimer(timeLeftMilliSeconds,countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                binding.apply {
                    timeLeftMilliSeconds = millisUntilFinished
                    val second = TimeUnit.MILLISECONDS.toSeconds(timeLeftMilliSeconds).toInt()

                    // %02d format the integer with 2 digit
                    val timer = String.format(Locale.getDefault(),"00: %02d",second)
                    quizTimer.text = timer

                    if(timeLeftMilliSeconds <10000){
                        quizTimer.setTextColor(Color.RED)
                    }else{
                        quizTimer.setTextColor(defaultColor)
                    }
                }
            }

            override fun onFinish() {
                showNextQuestion()
            }
        }.start()
    }

    private fun onRadioButtonClicked(clickedRadioButton: RadioButton) {
        binding.apply {
            // Stop the countdown timer
            countDownTimer?.cancel()

            val selectedAnswer = clickedRadioButton.text.toString()
            val correctAnswer = question[qIndex].correctAnswer

            // Check if the selected answer is correct
            val isCorrect = (selectedAnswer == correctAnswer)

            // Change the background color of the clicked radio button based on correctness
            clickedRadioButton.background = ContextCompat.getDrawable(
                requireActivity(),
                if (isCorrect) R.drawable.bg_right else R.drawable.bg_wrong
            )

            // Update score based on correctness
            if (isCorrect) {
                val timeTaken = countDownInMilliSecond - timeLeftMilliSeconds
                val secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(timeTaken).toInt()
                val scoreForThisQuestion = 10 + (20 - secondsRemaining)
                score += scoreForThisQuestion
                correct++
            } else {
                wrong++
            }
            qIndex++

            // Update the score display
            tvScore.text = "Score:$score"

            // Disable all radio buttons after a selection
            rbtn1.isEnabled = false
            rbtn2.isEnabled = false
            rbtn3.isEnabled = false
            rbtn4.isEnabled = false

            // Schedule the next question after a delay
            Handler(Looper.getMainLooper()).postDelayed({
                showNextQuestion()
            }, 3000) // Delay for 1 second before showing the next question
        }
    }

    private fun resetRadioButtonState() {
        binding.apply {
            rbtn1.isEnabled = true
            rbtn2.isEnabled = true
            rbtn3.isEnabled = true
            rbtn4.isEnabled = true

            rbtn1.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
            rbtn2.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
            rbtn3.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
            rbtn4.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
        }
    }

}
