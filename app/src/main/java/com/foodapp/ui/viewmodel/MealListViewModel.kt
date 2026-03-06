package com.foodapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodapp.data.model.CategoryUi
import com.foodapp.data.model.MealUi
import com.foodapp.data.repository.MealRepository
import com.foodapp.ui.state.MealListUiData
import com.foodapp.ui.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

class MealListViewModel(
    private val repository: MealRepository,
    private val pageSize: Int = 30
) : ViewModel() {

    private var allMeals: List<MealUi> = emptyList()
    private var currentPage = 0

    private var query: String = ""
    private var selectedCategory: String? = null
    private var categories: List<CategoryUi> = emptyList()

    private var mealsJob: Job? = null
    private var categoriesJob: Job? = null

    private val _state =
        MutableStateFlow<UiState<MealListUiData>>(UiState.Loading)

    val state: StateFlow<UiState<MealListUiData>> = _state

    fun loadInitial() {
        _state.value = UiState.Loading
        collectCategories()
        collectMealsFlow()
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
        selectedCategory = null
        collectMealsFlow()
    }

    fun onCategorySelected(category: String?) {
        selectedCategory = category
        query = ""
        collectMealsFlow()
    }

    fun loadNextPage() {
        val nextCount = (currentPage + 2) * pageSize
        if (nextCount <= allMeals.size) {
            currentPage++
            emitSuccess()
        }
    }

    fun retry() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.refreshCategories()
            repository.refreshMeals()
            collectCategories()
            collectMealsFlow()
        }
    }

    private fun collectCategories() {
        categoriesJob?.cancel()
        categoriesJob = viewModelScope.launch {
            repository.getCategories()
                .catch { /* pas bloquant */ }
                .collectLatest { cats ->
                    categories = cats
                    val cur = _state.value
                    if (cur is UiState.Success) {
                        _state.value = UiState.Success(cur.data.copy(categories = categories))
                    }
                }
        }
    }

    private fun collectMealsFlow() {
        mealsJob?.cancel()
        mealsJob = viewModelScope.launch {
            _state.value = UiState.Loading
            currentPage = 0

            val flow = when (val cat = selectedCategory) {
                null -> {
                    if (query.isBlank()) repository.getAllMeals()
                    else repository.searchMeals(query)
                }
                else -> repository.getMealsByCategory(cat)
            }

            flow
                .catch { e ->
                    _state.value = UiState.Error(e.message ?: "Erreur réseau")
                }
                .collectLatest { meals ->
                    allMeals = meals
                    emitSuccess()
                }
        }
    }

    private fun emitSuccess() {
        val visible = allMeals.take((currentPage + 1) * pageSize)
        _state.value = UiState.Success(
            MealListUiData(
                categories = categories,
                selectedCategory = selectedCategory,
                query = query,
                meals = visible,
                canLoadMore = visible.size < allMeals.size
            )
        )
    }
}