package com.foodapp.data.repository

import com.foodapp.data.local.AppDatabase
import com.foodapp.data.local.CategoryEntity
import com.foodapp.data.local.MealEntity
import com.foodapp.data.model.*
import com.foodapp.data.remote.MealApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepositoryImpl(
    private val api: MealApiService,
    private val db: AppDatabase
) : MealRepository {

    companion object {
        private const val CACHE_DURATION_MS = 60 * 60 * 1000L // 1 heure
    }


    // Lecture depuis le cache local

    override fun getAllMeals(): Flow<List<MealUi>> =
        db.mealDao().getAllMeals().map { it.map { entity -> entity.toMealUi() } }

    override fun searchMeals(query: String): Flow<List<MealUi>> =
        db.mealDao().searchMeals(query).map { it.map { entity -> entity.toMealUi() } }

    override fun getMealsByCategory(category: String): Flow<List<MealUi>> =
        db.mealDao().getMealsByCategory(category).map { it.map { entity -> entity.toMealUi() } }

    override suspend fun getMealById(id: String): MealDetailUi? {
        // 1. Essayer le cache local
        val cached = db.mealDao().getMealById(id)
        if (cached?.instructions != null) return cached.toMealDetailUi()

        // 2. Sinon appel réseau
        return try {
            val dto = api.getMealById(id).meals?.firstOrNull() ?: return null
            val entity = dto.toEntity()
            db.mealDao().upsertMeal(entity)
            entity.toMealDetailUi()
        } catch (e: Exception) {
            cached?.toMealDetailUi()
        }
    }

    override fun getCategories(): Flow<List<CategoryUi>> =
        db.categoryDao().getAllCategories().map { it.map { entity -> entity.toCategoryUi() } }

    // ═══════════════════════════════════════════════
    // Rafraîchissement réseau → cache
    // ═══════════════════════════════════════════════

    override suspend fun refreshMeals(): Result<Unit> = runCatching {
        val letters = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m")
        val allMeals = mutableListOf<MealEntity>()
        for (letter in letters) {
            val meals = api.searchMeals(letter).meals ?: emptyList()
            allMeals.addAll(meals.map { it.toEntity() })
        }
        db.mealDao().upsertMeals(allMeals)
    }

    override suspend fun refreshCategories(): Result<Unit> = runCatching {
        val categories = api.getCategories().categories
        db.categoryDao().upsertCategories(categories.map {
            CategoryEntity(
                id = it.idCategory,
                name = it.strCategory,
                thumbUrl = it.strCategoryThumb
            )
        })
    }

    override suspend fun needsRefresh(): Boolean {
        val oldest = db.mealDao().getOldestUpdateTimestamp() ?: return true
        return System.currentTimeMillis() - oldest > CACHE_DURATION_MS
    }
}

// ═══════════════════════════════════════════════
// Mappers Entity ↔ UI
// ═══════════════════════════════════════════════

fun MealEntity.toMealUi() = MealUi(
    id       = id,
    title    = title,
    imageUrl = imageUrl,
    category = category
)

fun MealEntity.toMealDetailUi() = MealDetailUi(
    id           = id,
    title        = title,
    imageUrl     = imageUrl,
    category     = category,
    area         = area,
    instructions = instructions,
    ingredients  = ingredients.split("|").filter { it.isNotBlank() }
)

fun CategoryEntity.toCategoryUi() = CategoryUi(
    id       = id,
    name     = name,
    thumbUrl = thumbUrl
)

fun MealDto.toEntity() = MealEntity(
    id           = idMeal,
    title        = strMeal,
    imageUrl     = strMealThumb,
    category     = strCategory,
    area         = strArea,
    instructions = strInstructions,
    ingredients  = buildIngredientList().joinToString("|")
)