package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MyTag"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    private var userScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate(Bundle?) called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.trueButton.setOnClickListener {view: View ->
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {view: View ->
            checkAnswer(false)
        }

        binding.prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            isAnswered(quizViewModel.currentIndex)
            updateQuestion()
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            isAnswered(quizViewModel.currentIndex)
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        updateQuestion()

        // Using Snackbar instead of Toast
        /*trueButton.setOnClickListener { view: View ->
            Snackbar.make(
                view,
                R.string.correct_toast,
                Snackbar.LENGTH_SHORT
            ).show()
        }

        falseButton.setOnClickListener { view: View ->
            Snackbar.make(
                view,
                R.string.incorrect_toast,
                Snackbar.LENGTH_SHORT
            ).show()
        }*/
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun isAnswered(index: Int) {
        val isQuestionAnswered = quizViewModel.currentQuestionAnswered
        binding.trueButton.isEnabled = !isQuestionAnswered
        binding.falseButton.isEnabled = !isQuestionAnswered
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        if (userAnswer == correctAnswer) {
            userScore += 1
        }

        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false
        quizViewModel.currentQuestionAnswered(true)

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        calculateScore()
    }

    private fun calculateScore() {
        for (item in quizViewModel.getQuestionBank()) {
            if (!item.answered) {
                return
            }
        }
        var score = userScore * 100 / quizViewModel.getQuestionBank().size
        var message = getString(R.string.toast_score, score)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}