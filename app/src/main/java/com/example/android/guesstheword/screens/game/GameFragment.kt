/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // If you create the ViewModel instance using the ViewModel class, a new object is created
        // every time the fragment is re-created.
        // Instead, create the ViewModel instance using a ViewModelProvider!!!
        Log.i("GameFragment", "Called ViewModelProvider.get")
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java) // can pass a factory to provider, in order to customize the content of created view model.
        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            binding.scoreText.text = newScore.toString()
        })
        viewModel.word.observe(viewLifecycleOwner, Observer { newWord ->
            binding.wordText.text = newWord
        })
        // This observer will have the issue described before `onEndGame()`, since the isGameFinished is still true after a screen rotation.
        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { isGameFinished ->
            if (isGameFinished) gameFinished()
        })

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        binding.correctButton.setOnClickListener { onCorrect() }
        binding.skipButton.setOnClickListener { onSkip() }
        binding.endGameButton.setOnClickListener( {_ -> onEndGame()} )
        return binding.root
    }

    /** the onEndGame method will be called when the user taps the End Game button */
    // The code I added had introduced a lifecycle issue.
    // Now the gameFinished will be triggered when recreate the view.

    // Reason: When the game fragment is re-created after a screen rotation, it moves from an
    // inactive to an active state. The observer in the fragment is re-connected to the existing
    // ViewModel and receives the current data. The gameFinished() method is re-triggered, and the
    // toast displays.

    // Solution: Add an onGameFinishComplete to set it to false after trigger the toast!!!
    private fun onEndGame() {
        gameFinished()
    }
    private fun gameFinished() {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        action.score = viewModel.score.value ?: 0
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete() // this is the solution
    }

    /** Methods for buttons presses **/
    // update score and word (both will be in viewmodel)
    private fun onSkip() {
        viewModel.onSkip()
    }

    private fun onCorrect() {
        viewModel.onCorrect()
    }

    // /** Methods for updating the UI **/
    // // the word should also get from viewModel
    // private fun updateWordText() {
    //     binding.wordText.text = viewModel.word.value
    // }
    //
    // private fun updateScoreText() {
    //     binding.scoreText.text = viewModel.score.value.toString()
    // }
}
