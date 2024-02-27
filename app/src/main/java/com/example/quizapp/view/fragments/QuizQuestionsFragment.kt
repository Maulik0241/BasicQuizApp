package com.example.quizapp.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.ContentValues.TAG
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
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.data.model.Question
import com.example.quizapp.databinding.FragmentQuizQuestionsBinding
import com.example.quizapp.utils.Constant.CORRECT
import com.example.quizapp.utils.Constant.OPTIONA
import com.example.quizapp.utils.Constant.OPTIONB
import com.example.quizapp.utils.Constant.OPTIONC
import com.example.quizapp.utils.Constant.OPTIOND
import com.example.quizapp.utils.Constant.SCORE
import com.example.quizapp.view.viewmodel.QuizViewModel
import java.util.Locale
import java.util.concurrent.TimeUnit

class QuizQuestionsFragment : Fragment() {

    private lateinit var binding: FragmentQuizQuestionsBinding
    private lateinit var viewModel: QuizViewModel
    private lateinit var question: List<Question>

    // Timer variables
    private var countDownTimer: CountDownTimer? = null
    private val countDownInMilliSecond: Long = 20000
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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQuizQuestionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(Application())
        )[QuizViewModel::class.java]


        viewModel.allQuestions.observe(requireActivity()) { questions ->
            questions.let {
                question = it
                initViews()
            }
        }
        viewModel.qIndex.observe(requireActivity()) {
            qIndex = it
        }
        viewModel.score.observe(requireActivity()) {
            score = it
        }
        viewModel.correct.observe(requireActivity()) {
            correct = it
        }
        viewModel.wrong.observe(requireActivity()) {
            wrong = it
        }
        viewModel.updateQueNo.observe(requireActivity()) {
            updateQueNo = it
        }

        viewModel.timeLeftMilliSeconds.observe(requireActivity()) {
            timeLeftMilliSeconds = it
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

    }

    /**
     * @showNextQuestion for Displays the next question.
     */
    @SuppressLint("SetTextI18n")
    private fun showNextQuestion() {
        resetRadioButtonState()
        binding.apply {
            if (updateQueNo <= 10) {
                tvNoOfQues.text = "${updateQueNo + 1}/10"
                updateQueNo++
            }
            if (qIndex < question.size ) {

                timeLeftMilliSeconds = countDownInMilliSecond
                statCountDownTimer()

                val correctOption = question[qIndex].correctAnswer
                val incorrectOptions = getRandomIncorrectOptions(
                    question[qIndex].optionA,
                    question[qIndex].optionB,
                    question[qIndex].optionC,
                    question[qIndex].optionD
                )
                val options = mutableListOf<String>()
                options.add(correctOption)
                options.addAll(incorrectOptions)
                options.shuffle()

                tvQuestion.text = question[qIndex].questionText
                rbtn1.text = options[0]
                rbtn2.text = options[1]
                rbtn3.text = options[2]
            } else {
                finishQuizOrNavigateToResultFragment()

            }
            radioGroup.clearCheck()
        }
    }

    /**
     * @getRandomIncorrectOptions for getting 2 random incorrect options from the available options.
     */
    private fun getRandomIncorrectOptions(vararg options: String): MutableList<String> {
        val incorrectOptions = mutableListOf<String>()
        val correctOptionIndex = options.indexOf(question[qIndex].correctAnswer)
        for (i in options.indices) {
            if (i != correctOptionIndex) {
                incorrectOptions.add(options[i])
            }
        }
        return if (incorrectOptions.size > 2) {
            incorrectOptions.shuffle()
            incorrectOptions.subList(0, 2)
        } else {
            mutableListOf(OPTIONA, OPTIONB, OPTIONC, OPTIOND)
        }

    }

    /**
     * @initView Initializes views and starts the quiz
     */
    @SuppressLint("SetTextI18n")
    private fun initViews() {

        binding.apply {
            if (qIndex < question.size) {
                tvQuestion.text = question[qIndex].questionText
                rbtn1.text = question[qIndex].optionA
                rbtn2.text = question[qIndex].optionB
                rbtn3.text = question[qIndex].optionC
            } else {
                return
            }

            rbtn1.setOnClickListener { onRadioButtonClicked(rbtn1) }
            rbtn2.setOnClickListener { onRadioButtonClicked(rbtn2) }
            rbtn3.setOnClickListener { onRadioButtonClicked(rbtn3) }

            tvNoOfQues.text = "$updateQueNo/10"
            tvQuestion.text = question[qIndex].questionText

            defaultColor = quizTimer.textColors


            timeLeftMilliSeconds = countDownInMilliSecond

            statCountDownTimer()
        }
    }

    /**
     * @stateCountDownTimer for  Starts or updates the countdown timer.
     */
    private fun statCountDownTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMilliSeconds, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                binding.apply {
                    timeLeftMilliSeconds = millisUntilFinished
                    val second = TimeUnit.MILLISECONDS.toSeconds(timeLeftMilliSeconds).toInt()

                    val timer = String.format(Locale.getDefault(), "00: %02d", second)
                    quizTimer.text = timer

                    if (timeLeftMilliSeconds < 10000) {
                        quizTimer.setTextColor(Color.RED)
                    } else {
                        quizTimer.setTextColor(defaultColor)
                    }
                }
            }

            override fun onFinish() {
                showNextQuestion()
            }
        }.start()
    }

    /**
     * @onRadioButtonClicked for Handles radio button clicks.
     */
    @SuppressLint("SetTextI18n")
    private fun onRadioButtonClicked(clickedRadioButton: RadioButton) {
        binding.apply {
            // Stop the countdown timer
            countDownTimer?.cancel()

            val selectedAnswer = clickedRadioButton.text.toString()
            val correctAnswer = question[qIndex].correctAnswer

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
                Log.e(TAG, "correct: $correct", )
            } else {
                wrong++
                Log.e(TAG, "wrong: $wrong", )
            }
            qIndex++

            // Update the score display
            tvScore.text = "Score:$score"

            // Disable all radio buttons after a selection
            rbtn1.isEnabled = false
            rbtn2.isEnabled = false
            rbtn3.isEnabled = false

            // Schedule the next question after a delay
            Handler(Looper.getMainLooper()).postDelayed({
                showNextQuestion()
            }, 1000) // Delay for 1 second before showing the next question
        }
    }

    /**
     * @resetRadioButtonState for Resets the state of radio buttons.
     */
    private fun resetRadioButtonState() {
        if (isAdded) {
            binding.apply {
                rbtn1.isEnabled = true
                rbtn2.isEnabled = true
                rbtn3.isEnabled = true

                rbtn1.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
                rbtn2.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
                rbtn3.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg)
            }
        }
    }

    /**
     * @finishQuizOrNavigateToResultFragment for navigate to Result Screen.
     */
    private fun finishQuizOrNavigateToResultFragment() {
        val bundle = Bundle()
        bundle.putInt(CORRECT, correct)
        bundle.putInt(SCORE, score)
        findNavController().navigate(R.id.action_quizQuestionsFragment_to_resultFragment, bundle)
    }

    /**
     * @showExistConfirmationDialog AlertDialog box for handling navigation
     */
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit Quiz")
            .setMessage("Are you sure you want to exit the quiz?")
            .setPositiveButton("Yes") { _, _ ->
                // User clicked Yes, navigate to StartQuizScreen
                navigateToStartQuizScreen()
            }
            .setNegativeButton("No", null)
            .show()
    }

    /**
     * @navigateToStartQuizScreen navigate to start_quiz screen from quiz_question_screen
     */
    private fun navigateToStartQuizScreen() {
        // Navigate to StartQuizScreen using NavController
        findNavController().navigate(R.id.action_quizQuestionsFragment_to_startQuizFragment)
    }


}
