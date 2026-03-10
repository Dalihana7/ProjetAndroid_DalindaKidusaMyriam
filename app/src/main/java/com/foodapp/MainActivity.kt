package com.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.foodapp.data.local.AppDatabase
import com.foodapp.data.remote.RetrofitClient
import com.foodapp.data.repository.MealRepositoryImpl
import com.foodapp.ui.Screen
import com.foodapp.ui.screens.MealDetailScreen
import com.foodapp.ui.screens.MealListScreen
import com.foodapp.ui.screens.SplashScreen
import com.foodapp.ui.state.UiState
import com.foodapp.ui.theme.FoodAppTheme
import com.foodapp.ui.viewmodel.MealDetailViewModel
import com.foodapp.ui.viewmodel.MealListViewModel
import com.foodapp.worker.RefreshMealsWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val request = PeriodicWorkRequestBuilder<RefreshMealsWorker>(
            24, TimeUnit.HOURS
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "refresh_meals",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        val db = AppDatabase.getInstance(this)
        val api = RetrofitClient.apiService
        val repository = MealRepositoryImpl(api, db)

        val listViewModel = MealListViewModel(repository)
        val detailViewModel = MealDetailViewModel(repository)

        setContent {
            FoodAppTheme {
                AppNavigation(listViewModel, detailViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(
    listViewModel: MealListViewModel,
    detailViewModel: MealDetailViewModel
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    val listState by listViewModel.state.collectAsStateWithLifecycle()
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        listViewModel.loadInitial()
    }

    when (currentScreen) {
        is Screen.Splash -> {
            SplashScreen(onSplashFinished = { currentScreen = Screen.MealList })
        }
        is Screen.MealList -> {
            when (val state = listState) {
                is UiState.Loading -> {
                    MealListScreen(
                        mealsState = UiState.Loading,
                        categories = emptyList(),
                        selectedCategory = null,
                        searchQuery = "",
                        onSearchQueryChange = { listViewModel.onQueryChange(it) },
                        onCategorySelected = { listViewModel.onCategorySelected(it) },
                        onMealClick = {
                            currentScreen = Screen.MealDetail(it)
                            detailViewModel.load(it)
                        },
                        onRetry = { listViewModel.retry() },
                        onLoadMore = { listViewModel.loadNextPage() }
                    )
                }
                is UiState.Error -> {
                    MealListScreen(
                        mealsState = UiState.Error(state.message),
                        categories = emptyList(),
                        selectedCategory = null,
                        searchQuery = "",
                        onSearchQueryChange = { listViewModel.onQueryChange(it) },
                        onCategorySelected = { listViewModel.onCategorySelected(it) },
                        onMealClick = {
                            currentScreen = Screen.MealDetail(it)
                            detailViewModel.load(it)
                        },
                        onRetry = { listViewModel.retry() },
                        onLoadMore = { listViewModel.loadNextPage() }
                    )
                }
                is UiState.Success -> {
                    MealListScreen(
                        mealsState = UiState.Success(state.data.meals),
                        categories = state.data.categories,
                        selectedCategory = state.data.selectedCategory,
                        searchQuery = state.data.query,
                        onSearchQueryChange = { listViewModel.onQueryChange(it) },
                        onCategorySelected = { listViewModel.onCategorySelected(it) },
                        onMealClick = {
                            currentScreen = Screen.MealDetail(it)
                            detailViewModel.load(it)
                        },
                        onRetry = { listViewModel.retry() },
                        onLoadMore = { listViewModel.loadNextPage() }
                    )
                }
            }
        }
        is Screen.MealDetail -> {
            when (val state = detailState) {
                is UiState.Loading -> {
                    MealDetailScreen(
                        mealState = UiState.Loading,
                        onBack = { currentScreen = Screen.MealList },
                        onRetry = {
                            detailViewModel.load((currentScreen as Screen.MealDetail).mealId)
                        }
                    )
                }
                is UiState.Error -> {
                    MealDetailScreen(
                        mealState = UiState.Error(state.message),
                        onBack = { currentScreen = Screen.MealList },
                        onRetry = {
                            detailViewModel.load((currentScreen as Screen.MealDetail).mealId)
                        }
                    )
                }
                is UiState.Success -> {
                    MealDetailScreen(
                        mealState = UiState.Success(state.data),
                        onBack = { currentScreen = Screen.MealList },
                        onRetry = {
                            detailViewModel.load((currentScreen as Screen.MealDetail).mealId)
                        }
                    )
                }
            }
        }
    }
}