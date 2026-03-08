package com.foodapp.data.model

// DTOs — Réponses brutes de l'API

data class MealResponse(
    val meals: List<MealDto>?
)

data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strMealThumb: String?,
    val strInstructions: String?,
    val strIngredient1: String?,  val strMeasure1: String?,
    val strIngredient2: String?,  val strMeasure2: String?,
    val strIngredient3: String?,  val strMeasure3: String?,
    val strIngredient4: String?,  val strMeasure4: String?,
    val strIngredient5: String?,  val strMeasure5: String?,
    val strIngredient6: String?,  val strMeasure6: String?,
    val strIngredient7: String?,  val strMeasure7: String?,
    val strIngredient8: String?,  val strMeasure8: String?,
    val strIngredient9: String?,  val strMeasure9: String?,
    val strIngredient10: String?, val strMeasure10: String?,
    val strIngredient11: String?, val strMeasure11: String?,
    val strIngredient12: String?, val strMeasure12: String?,
    val strIngredient13: String?, val strMeasure13: String?,
    val strIngredient14: String?, val strMeasure14: String?,
    val strIngredient15: String?, val strMeasure15: String?,
    val strIngredient16: String?, val strMeasure16: String?,
    val strIngredient17: String?, val strMeasure17: String?,
    val strIngredient18: String?, val strMeasure18: String?,
    val strIngredient19: String?, val strMeasure19: String?,
    val strIngredient20: String?, val strMeasure20: String?,
)

data class CategoryResponse(
    val categories: List<CategoryDto>
)

data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String
)

// ═══════════════════════════════════════════════
// Modèles UI — Partagés avec Kidusa et Dalinda
// ═══════════════════════════════════════════════

data class MealUi(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val category: String?
)

data class MealDetailUi(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val ingredients: List<String>
)

data class CategoryUi(
    val id: String,
    val name: String,
    val thumbUrl: String
)

// ═══════════════════════════════════════════════
// Mappers DTO → UI
// ═══════════════════════════════════════════════

fun MealDto.toMealUi() = MealUi(
    id       = idMeal,
    title    = strMeal,
    imageUrl = strMealThumb,
    category = strCategory
)

fun MealDto.toMealDetailUi() = MealDetailUi(
    id           = idMeal,
    title        = strMeal,
    imageUrl     = strMealThumb,
    category     = strCategory,
    area         = strArea,
    instructions = strInstructions,
    ingredients  = buildIngredientList()
)

fun CategoryDto.toCategoryUi() = CategoryUi(
    id       = idCategory,
    name     = strCategory,
    thumbUrl = strCategoryThumb
)

fun MealDto.buildIngredientList(): List<String> {
    val pairs = listOf(
        strIngredient1  to strMeasure1,  strIngredient2  to strMeasure2,
        strIngredient3  to strMeasure3,  strIngredient4  to strMeasure4,
        strIngredient5  to strMeasure5,  strIngredient6  to strMeasure6,
        strIngredient7  to strMeasure7,  strIngredient8  to strMeasure8,
        strIngredient9  to strMeasure9,  strIngredient10 to strMeasure10,
        strIngredient11 to strMeasure11, strIngredient12 to strMeasure12,
        strIngredient13 to strMeasure13, strIngredient14 to strMeasure14,
        strIngredient15 to strMeasure15, strIngredient16 to strMeasure16,
        strIngredient17 to strMeasure17, strIngredient18 to strMeasure18,
        strIngredient19 to strMeasure19, strIngredient20 to strMeasure20,
    )
    return pairs
        .filter { (ingredient, _) -> !ingredient.isNullOrBlank() }
        .map    { (ingredient, measure) ->
            "${measure?.trim().orEmpty()} ${ingredient!!.trim()}".trim()
        }
}