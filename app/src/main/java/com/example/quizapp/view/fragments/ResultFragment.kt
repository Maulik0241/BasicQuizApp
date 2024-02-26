package com.example.quizapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val score = requireArguments().getInt("score",0)
        val correct = requireArguments().getInt("correct",0)

        binding.tvScore.text ="Your Score: $score"

        binding.tvMessage.text =when(correct){
            5 -> "You Won! Congratulations."
            7 -> "You Won! Congratulations."
            9 -> "You Won! Congratulations and Well Done."
            10 -> "Awesome. You are Genius"
            else->"Better luck next time!!"
        }

        binding.playQuizBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_quizQuestionsFragment)
        }

        binding.playQuizHomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_startQuizFragment)
        }

    }

}