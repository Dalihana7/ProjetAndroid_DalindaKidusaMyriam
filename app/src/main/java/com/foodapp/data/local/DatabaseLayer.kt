package com.foodapp.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Entités Room

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val imageUrl: String?,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val ingredients: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val thumbUrl: String,
    val lastUpdated: Long = System.currentTimeMillis()
)


// DAOs


@Dao
interface MealDao {

    @Query("SELECT * FROM meals ORDER BY title ASC")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchMeals(query: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE category = :category ORDER BY title ASC")
    fun getMealsByCategory(category: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: String): MealEntity?

    @Upsert
    suspend fun upsertMeals(meals: List<MealEntity>)

    @Upsert
    suspend fun upsertMeal(meal: MealEntity)

    @Query("SELECT MIN(lastUpdated) FROM meals")
    suspend fun getOldestUpdateTimestamp(): Long?
}

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    @Query("SELECT MIN(lastUpdated) FROM categories")
    suspend fun getOldestUpdateTimestamp(): Long?
}


// Base de données


@Database(
    entities = [MealEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DATABASE_NAME = "food_app_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}