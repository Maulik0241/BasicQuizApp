package com.example.quizapp.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentResultBinding
import com.example.quizapp.utils.Constant.CORRECT
import com.example.quizapp.utils.Constant.SCORE

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val score = requireArguments().getInt(SCORE, 0)
        val correct = requireArguments().getInt(CORRECT, 0)

        binding.tvScore.text = "Your Score: $score"
        binding.tvCorrect.text = "Your Corrects: $correct/10"

        binding.tvMessage.text = when (correct) {
            0, 1, 2 -> getString(R.string.sorry_failed)
            3, 4 -> getString(R.string.well_played)
            5,6 -> getString(R.string.you_won)
            7,8 -> getString(R.string.congratulation)
            9 -> getString(R.string.well_done)
            10 -> getString(R.string.awesome)
            else -> getString(R.string.better_luck)
        }

        binding.playQuizBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_quizQuestionsFragment)
        }

        binding.playQuizHomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_startQuizFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

    }

    /**
     * @showExistConfirmationDialog AlertDialog box for handle exit app from current screen
     */
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