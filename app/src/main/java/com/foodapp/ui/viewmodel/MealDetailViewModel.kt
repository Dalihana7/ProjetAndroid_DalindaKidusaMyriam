package com.foodapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodapp.data.model.MealDetailUi
import com.foodapp.data.repository.MealRepository
import com.foodapp.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealDetailViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<MealDetailUi>>(UiState.Loading)
    val state: StateFlow<UiState<MealDetailUi>> = _state.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { repository.getMealById(id) }
                .onSuccess { detail ->
                    if (detail == null) {
                        _state.value = UiState.Error("Recette introuvable")
                    } else {
                        _state.value = UiState.Success(detail)
                    }
                }
                .onFailure { e ->
                    _state.value = UiState.Error(e.message ?: "Erreur réseau")
                }
        }
    }
}