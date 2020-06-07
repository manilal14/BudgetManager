package com.mani.budgetmanager.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mani.budgetmanager.Expense.ExpenseModel
import com.mani.budgetmanager.Income.IncomeModel


@Dao
interface IncomeDao {

//    @Query("SELECT * FROM IncomeModel")
//    fun getAllIncomes() : MutableList<IncomeModel>

    @Query("SELECT * FROM IncomeModel WHERE IncomeModel.month = :month")
    fun getIncomeByMonth(month : String) : MutableList<IncomeModel>

    @Query("SELECT IncomeModel.amount FROM IncomeModel WHERE IncomeModel.month = :month")
    fun getTotalMonthlyIncome(month: String) : List<Int>

    @Insert
    fun insert(income : IncomeModel)

    @Query("DELETE FROM IncomeModel WHERE id = :id")
    fun removeById(id : Int)

    @Query("DELETE FROM IncomeModel WHERE IncomeModel.month = :month")
    fun deleteCurrentMonthIncome(month: String)
}

