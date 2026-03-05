package com.foodapp.repository.fake


import kotlinx.coroutines.delay
import com.foodapp.data.model.*
import com.foodapp.data.repository.MealRepository

class FakeMealRepository : MealRepository {

    private val categories = listOf(
        CategoryUi("Beef", "https://www.themealdb.com/images/category/beef.png"),
        CategoryUi("Chicken", "https://www.themealdb.com/images/category/chicken.png"),
        CategoryUi("Seafood", "https://www.themealdb.com/images/category/seafood.png"),
    )

    private val meals = (1..120).map {
        MealUi(
            id = it.toString(),
            title = "Recipe $it",
            imageUrl = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
            category = listOf("Beef", "Chicken", "Seafood")[it % 3]
        )
    }

    override suspend fun searchMealsByName(query: String): List<MealUi> {
        delay(400) // simule réseau
        if (query == "error") throw RuntimeException("Network error")
        if (query.isBlank()) return meals
        return meals.filter { it.title.contains(query, ignoreCase = true) }
    }

    override suspend fun getCategories(): List<CategoryUi> {
        delay(200)
        return categories
    }

    override suspend fun getMealsByCategory(category: String): List<MealUi> {
        delay(300)
        return meals.filter { it.category.equals(category, true) }
    }

    override suspend fun getMealDetail(id: String): MealDetailUi {
        delay(250)
        val base = meals.first { it.id == id }
        return MealDetailUi(
            id = base.id,
            title = base.title,
            imageUrl = base.imageUrl,
            category = base.category ?: "Unknown",
            instructions = "Step 1... Step 2... Step 3...",
            ingredients = listOf(
                IngredientUi("Soy sauce", "3/4 cup"),
                IngredientUi("Water", "1/2 cup")
            )
        )
    }
}