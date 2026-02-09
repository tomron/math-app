package com.mathmaster.app.ui.games

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Base ViewModel for game screens.
 * Manages difficulty selection and common game state.
 */
abstract class BaseGameViewModel : ViewModel() {

    protected val _difficulty = MutableStateFlow(Difficulty.EASY)
    val difficulty: StateFlow<Difficulty> = _difficulty.asStateFlow()

    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.update { difficulty }
    }
}
