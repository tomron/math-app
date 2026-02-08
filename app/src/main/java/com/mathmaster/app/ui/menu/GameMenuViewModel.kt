package com.mathmaster.app.ui.menu

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameMenuUiState(
    val games: List<GameDefinition> = allGames,
    val selectedProfile: String = ""
)

class GameMenuViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameMenuUiState())
    val uiState: StateFlow<GameMenuUiState> = _uiState.asStateFlow()

    fun setSelectedProfile(profileName: String) {
        _uiState.value = _uiState.value.copy(selectedProfile = profileName)
    }
}
