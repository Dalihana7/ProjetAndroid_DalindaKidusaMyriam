package com.foodapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.foodapp.data.local.AppDatabase
import com.foodapp.data.remote.RetrofitClient
import com.foodapp.data.repository.MealRepositoryImpl

class RefreshMealsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val api = RetrofitClient.apiService
            val repository = MealRepositoryImpl(api, db)

            if (repository.needsRefresh()) {
                repository.refreshCategories()
                repository.refreshMeals()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}