package com.mani.budgetmanager.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mani.budgetmanager.Expense.ExpenseModel


@Dao
interface ExpenseDao {

//
//    @Query("SELECT * FROM ExpenseModel")
//    fun getAllExpense() : MutableList<ExpenseModel>


    @Query("SELECT * FROM ExpenseModel WHERE ExpenseModel.month = :month")
    fun getExpenseByMonth(month : String) : MutableList<ExpenseModel>

    @Query("SELECT ExpenseModel.amount FROM ExpenseModel WHERE ExpenseModel.month = :month")
    fun getTotalMonthlyExpense(month: String) : List<Int>


    @Insert
    fun insert(expense : ExpenseModel)

    @Query("DELETE FROM ExpenseModel WHERE id = :id")
    fun removeById(id : Int)

    @Query("DELETE FROM ExpenseModel WHERE ExpenseModel.month = :month")
    fun deleteCurrentMonthExpemse(month: String)
}

