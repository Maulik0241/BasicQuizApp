package com.example.quizapp.view.fragments

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.data.QuestionsDatabase
import com.example.quizapp.databinding.FragmentStartQuizBinding
import com.example.quizapp.utils.SampleQuestions
import com.example.quizapp.view.viewmodel.StartQuizViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartQuizFragment : Fragment() {

    private lateinit var binding: FragmentStartQuizBinding
    private lateinit var viewModel: StartQuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartQuizBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(Application())
        )[StartQuizViewModel::class.java]

        // initialize the Room database
        val questions = SampleQuestions.sampleQuestions
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.addAllQuestion(questions)
        }

        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.action_startQuizFragment_to_quizQuestionsFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            showExitConfirmationDialog()
        }
    }


    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the App?")
            .setPositiveButton("Yes") { _, _ ->
                // User clicked Yes, navigate to StartQuizScreen
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}