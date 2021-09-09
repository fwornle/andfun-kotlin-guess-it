package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController

class ScoreViewModel(finalScore: Int): ViewModel() {

    // The current score
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    // The current state of "play again" button
    private var _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean> get() = _eventPlayAgain

    init {
        // initialize LiveData
        _score.value = finalScore
        _eventPlayAgain.value = false

        Log.i("ScoreViewModel", "Final Score is: $finalScore")
    }

    // set state associated w/h playAgain button
    fun onPlayAgain() {
        _eventPlayAgain.value = true
    }

    // ... called when navigation to the Game screen has happened
    fun onPlayAgainComplete() {
        _eventPlayAgain.value = false
    }

}