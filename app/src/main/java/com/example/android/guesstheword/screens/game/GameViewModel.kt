package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
  // The livedata should be used as mutable inside ViewModel, but expose as immutable LiveData
  // The current word
  private val _word = MutableLiveData<String>()
  val word: LiveData<String>
    get() = _word

  // The current score
  private val _score = MutableLiveData<Int>()
  val score: LiveData<Int>
    get() {
      return _score
    }

  // uses LiveData to trigger a game-finished event
// Event which triggers the end of the game
  private val _eventGameFinish = MutableLiveData<Boolean>()
  val eventGameFinish: LiveData<Boolean>
    get() = _eventGameFinish

  // The list of words - the front of the list is the next word to guess
  private lateinit var wordList: MutableList<String>

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

  init {
    _word.value = ""
    _score.value = 0
    _eventGameFinish.value = false
    resetList()
    nextWord()
    Log.i("GameViewModel", "GameViewModel created!")
  }

  /** before destroyed, the onCleared() is called to clean up the resources */
  override fun onCleared() {
    super.onCleared()
    Log.i("GameViewModel", "GameViewModel destroyed!")
  }

  /** Methods for buttons presses **/
  // update score and word (both will be in viewmodel)
  fun onSkip() {
    _score.value = _score.value?.minus(1)
    nextWord()
  }

  fun onCorrect() {
    _score.value = _score.value?.plus(1)
    nextWord()
  }

  /**
   * Moves to the next word in the list
   */
  private fun nextWord() {
    if (!wordList.isEmpty()) {
      //Select and remove a word from the list
      _word.value = wordList.removeAt(0)
    } else {
      onGameFinish()
    }
    // don't do the UI update in viewModel.
    // updateWordText()
    // updateScoreText()
  }

  fun onGameFinish() {
    _eventGameFinish.value = true
  }

  fun onGameFinishComplete() {
    _eventGameFinish.value = false
  }
}