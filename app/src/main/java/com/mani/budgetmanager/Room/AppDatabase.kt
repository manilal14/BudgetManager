package com.mani.budgetmanager.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mani.budgetmanager.Expense.ExpenseModel
import com.mani.budgetmanager.Income.IncomeModel

@Database(entities = [ExpenseModel::class, IncomeModel::class], version = 2)

abstract class AppDatabase : RoomDatabase() {

    abstract val expenseDao : ExpenseDao
    abstract val incomeDao : IncomeDao



    companion object {

        private val DB_NAME = "BudgetDatabase.db"
        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = create(context)
            }
            return instance
        }

        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}