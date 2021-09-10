/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// buzzer patterns (delay, duration, delay, ...)
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel: ViewModel() {

    // buzzer
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    // timer
    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the "panic" time of the game
        const val COUNTDOWN_PANIC_SECONDS = 5000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 20000L
    }

    // declare timer variable
    private val timer: CountDownTimer

    // The current word
    private var _word =  MutableLiveData<String>()
    val word:LiveData<String> get() = _word

    // The current score
    private var _score = MutableLiveData<Int>()
    val score:LiveData<Int> get() = _score

    // The current "game finished" state
    private var _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish:LiveData<Boolean> get() = _eventGameFinish

    // The current "buzz" event
    private var _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz:LiveData<BuzzType> get() = _eventBuzz

    // The current timer value
    private var _currentTime = MutableLiveData<Long>()

    // formatted timer value
    private var _currentTimeString = Transformations.map(_currentTime)
        { time -> DateUtils.formatElapsedTime(time) }
    val currentTimeString:LiveData<String> get() = _currentTimeString

    // The list of words - the front of the list is the next word to guess
    lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel created.")
        resetList()

        // initialize LiveData objects
        _score.value = 0
        _word.value = wordList.removeAt(0)
        _eventGameFinish.value = false
        _eventBuzz.value = BuzzType.NO_BUZZ
        _currentTime.value = COUNTDOWN_TIME

        // create timer object
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                // set timer display value to remainder of timer (arg)
                _currentTime.value = millisUntilFinished / ONE_SECOND

                // during "panic" time (last 5 seconds) the panic buzzer is sounded
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                // set timer value to "0" (is this really needed?!)
                _currentTime.value = DONE

                // game over buzzer
                _eventBuzz.value = BuzzType.GAME_OVER

                // signal game over (to fragment) --> triggers transition
                _eventGameFinish.value = true
            }
        }

        timer.start()
    }


    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            // start anew - same words, shuffled
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel", "GameViewModel destroyed.")
    }
}


