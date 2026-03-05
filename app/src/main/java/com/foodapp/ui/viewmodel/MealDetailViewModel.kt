package com.foodapp.ui.viewmodel

package your.package.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import your.package.domain.model.MealDetailUi
import your.package.domain.repository.MealRepository
import your.package.ui.state.UiState

class MealDetailViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<MealDetailUi>>(UiState.Loading)
    val state: StateFlow<UiState<MealDetailUi>> = _state.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { repository.getMealDetail(id) }
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Erreur réseau") }
        }
    }
}