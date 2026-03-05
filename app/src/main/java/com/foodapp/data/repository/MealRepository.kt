package com.foodapp.data.repository

import com.foodapp.data.model.CategoryUi
import com.foodapp.data.model.MealDetailUi
import com.foodapp.data.model.MealUi
import kotlinx.coroutines.flow.Flow

// Interface utilisée par Kidusa dans ses ViewModels
interface MealRepository {

    // Toutes les recettes
    fun getAllMeals(): Flow<List<MealUi>>

    // Recherche par titre
    fun searchMeals(query: String): Flow<List<MealUi>>

    // Filtre par catégorie
    fun getMealsByCategory(category: String): Flow<List<MealUi>>

    // Détail d'une recette par ID
    suspend fun getMealById(id: String): MealDetailUi?

    // Liste des catégories
    fun getCategories(): Flow<List<CategoryUi>>

    // Rafraîchir les données depuis le réseau
    suspend fun refreshMeals(): Result<Unit>
    suspend fun refreshCategories(): Result<Unit>

    // Vérifier si le cache est périmé
    suspend fun needsRefresh(): Boolean
}