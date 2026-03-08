package com.foodapp.data.repository

import com.foodapp.data.model.CategoryUi
import com.foodapp.data.model.MealDetailUi
import com.foodapp.data.model.MealUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeMealRepository : MealRepository {

    private val categories = listOf(
        CategoryUi(
            id = "1",
            name = "Beef",
            thumbUrl = "https://www.themealdb.com/images/category/beef.png"
        ),
        CategoryUi(
            id = "2",
            name = "Chicken",
            thumbUrl = "https://www.themealdb.com/images/category/chicken.png"
        ),
        CategoryUi(
            id = "3",
            name = "Seafood",
            thumbUrl = "https://www.themealdb.com/images/category/seafood.png"
        )
    )

    private val meals = (1..120).map {
        MealUi(
            id = it.toString(),
            title = "Recipe $it",
            imageUrl = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
            category = listOf("Beef", "Chicken", "Seafood")[it % 3]
        )
    }

    override fun getAllMeals(): Flow<List<MealUi>> = flow {
        delay(300)
        emit(meals)
    }

    override fun searchMeals(query: String): Flow<List<MealUi>> = flow {
        delay(300)
        if (query == "error") throw RuntimeException("Erreur réseau simulée")
        emit(meals.filter { it.title.contains(query, ignoreCase = true) })
    }

    override fun getMealsByCategory(category: String): Flow<List<MealUi>> = flow {
        delay(300)
        emit(meals.filter { it.category.equals(category, ignoreCase = true) })
    }

    override suspend fun getMealById(id: String): MealDetailUi? {
        delay(200)
        val meal = meals.firstOrNull { it.id == id } ?: return null
        return MealDetailUi(
            id = meal.id,
            title = meal.title,
            imageUrl = meal.imageUrl,
            category = meal.category,
            area = "Japanese",
            instructions = "Step 1... Step 2... Step 3...",
            ingredients = listOf(
                "Soy sauce - 3/4 cup",
                "Water - 1/2 cup"
            )
        )
    }

    override fun getCategories(): Flow<List<CategoryUi>> = flowOf(categories)

    override suspend fun refreshMeals(): Result<Unit> = Result.success(Unit)

    override suspend fun refreshCategories(): Result<Unit> = Result.success(Unit)

    override suspend fun needsRefresh(): Boolean = false
}