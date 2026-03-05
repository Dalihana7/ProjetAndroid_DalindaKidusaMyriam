package com.foodapp.ui.viewmodel

package your.package.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import your.package.domain.model.MealUi
import your.package.domain.repository.MealRepository
import your.package.ui.state.UiState

data class MealListUiData(
    val categories: List<String>,
    val selectedCategory: String?,
    val query: String,
    val meals: List<MealUi>,
    val canLoadMore: Boolean
)

class MealListViewModel(
    private val repository: MealRepository,
    private val pageSize: Int = 30
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    private var allMeals: List<MealUi> = emptyList()
    private var currentPage = 0

    private val _state = MutableStateFlow<UiState<MealListUiData>>(UiState.Loading)
    val state: StateFlow<UiState<MealListUiData>> = _state.asStateFlow()

    fun loadInitial() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                val cats = repository.getCategories().map { it.name }
                allMeals = repository.searchMealsByName("")
                currentPage = 0
                buildSuccess(cats)
            }.onFailure { e ->
                _state.value = UiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        refreshMeals()
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        refreshMeals()
    }

    fun loadNextPage() {
        val nextSize = (currentPage + 2) * pageSize
        if (nextSize <= allMeals.size) {
            currentPage++
            val current = _state.value
            if (current is UiState.Success) {
                _state.value = UiState.Success(current.data.copy(
                    meals = allMeals.take((currentPage + 1) * pageSize),
                    canLoadMore = allMeals.size > (currentPage + 1) * pageSize
                ))
            }
        }
    }

    fun retry() = refreshMeals(force = true)

    private fun refreshMeals(force: Boolean = false) {
        viewModelScope.launch {
            val previousCats = (state.value as? UiState.Success)?.data?.categories ?: emptyList()
            _state.value = UiState.Loading

            runCatching {
                val q = _query.value.trim()
                val cat = _selectedCategory.value

                allMeals = when {
                    cat != null -> repository.getMealsByCategory(cat)
                    else -> repository.searchMealsByName(q)
                }

                currentPage = 0
                val mealsPage = allMeals.take(pageSize)

                UiState.Success(
                    MealListUiData(
                        categories = previousCats,
                        selectedCategory = cat,
                        query = q,
                        meals = mealsPage,
                        canLoadMore = allMeals.size > pageSize
                    )
                )
            }.onSuccess { _state.value = it }
                .onFailure { e -> _state.value = UiState.Error(e.message ?: "Erreur réseau") }
        }
    }

    private fun buildSuccess(categories: List<String>) {
        val mealsPage = allMeals.take(pageSize)
        _state.value = UiState.Success(
            MealListUiData(
                categories = categories,
                selectedCategory = _selectedCategory.value,
                query = _query.value,
                meals = mealsPage,
                canLoadMore = allMeals.size > pageSize
            )
        )
    }
}