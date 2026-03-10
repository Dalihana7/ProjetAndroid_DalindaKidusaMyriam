package com.foodapp.ui

import com.foodapp.data.model.MealDetailUi
import com.foodapp.data.model.MealUi
import com.foodapp.data.model.CategoryUi

sealed class Screen {
    object Splash : Screen()
    object MealList : Screen()
    data class MealDetail(val mealId: String) : Screen()
}