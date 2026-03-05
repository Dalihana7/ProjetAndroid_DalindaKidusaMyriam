package com.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.foodapp.data.model.CategoryUi
import com.foodapp.data.model.MealDetailUi
import com.foodapp.data.model.MealUi
import com.foodapp.ui.Screen
import com.foodapp.ui.screens.MealDetailScreen
import com.foodapp.ui.screens.MealListScreen
import com.foodapp.ui.screens.SplashScreen
import com.foodapp.ui.theme.FoodAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    // Données mock pour tester l'UI en attendant Kidusa
    val mockMeals = listOf(
        MealUi("1", "Spaghetti Bolognese", "https://www.themealdb.com/images/media/meals/sutysw1468247559.jpg", "Pasta"),
        MealUi("2", "Chicken Curry", "https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg", "Chicken"),
        MealUi("3", "Beef Tacos", "https://www.themealdb.com/images/media/meals/basvrp1493160694.jpg", "Beef"),
        MealUi("4", "Caesar Salad", "https://www.themealdb.com/images/media/meals/qi33651565192851.jpg", "Vegetarian"),
        MealUi("5", "Pad Thai", "https://www.themealdb.com/images/media/meals/uuuspp1511297945.jpg", "Chicken"),
    )

    val mockCategories = listOf(
        CategoryUi("1", "Beef", ""),
        CategoryUi("2", "Chicken", ""),
        CategoryUi("3", "Pasta", ""),
        CategoryUi("4", "Vegetarian", ""),
    )

    val mockDetail = MealDetailUi(
        id = "1",
        title = "Spaghetti Bolognese",
        imageUrl = "https://www.themealdb.com/images/media/meals/sutysw1468247559.jpg",
        category = "Pasta",
        area = "Italian",
        instructions = "Cuire les pâtes. Préparer la sauce bolognese. Mélanger et servir.",
        ingredients = listOf("500g Spaghetti", "300g Bœuf haché", "2 Tomates", "1 Oignon", "Ail", "Huile d'olive")
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        is Screen.Splash -> {
            SplashScreen(onSplashFinished = { currentScreen = Screen.MealList })
        }
        is Screen.MealList -> {
            MealListScreen(
                mealsState = UiState.Success(mockMeals),
                categories = mockCategories,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onCategorySelected = { selectedCategory = it },
                onMealClick = { currentScreen = Screen.MealDetail(it) },
                onRetry = {},
                onLoadMore = {}
            )
        }
        is Screen.MealDetail -> {
            MealDetailScreen(
                mealState = UiState.Success(mockDetail),
                onBack = { currentScreen = Screen.MealList },
                onRetry = {}
            )
        }
    }
}