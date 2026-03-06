package com.foodapp.ui.state

import com.foodapp.data.model.CategoryUi
import com.foodapp.data.model.MealUi

data class MealListUiData(
    val query: String,
    val selectedCategory: String?,
    val categories: List<CategoryUi>,
    val meals: List<MealUi>,
    val canLoadMore: Boolean
)