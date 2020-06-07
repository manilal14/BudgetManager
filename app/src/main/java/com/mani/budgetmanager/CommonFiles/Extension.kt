package com.mani.budgetmanager.CommonFiles

import android.content.Context
import android.widget.Toast
import com.mani.budgetmanager.R
import java.text.DecimalFormat
import java.text.NumberFormat
import com.mani.budgetmanager.CommonFiles.AppConstants.Companion.CategoryExpense
import com.mani.budgetmanager.CommonFiles.AppConstants.Companion.CategoryIncome

fun Context.showToast(message : String ){
    Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
}


fun formatMoney(amount:Int) : String{
    val formatter: NumberFormat = DecimalFormat("#,##,###")
    return "₹ " + formatter.format(amount)
}


fun getDrawableImageId(category: String) : Int {

    when(category){

        CategoryExpense.Rent.name       -> return R.drawable.rent
        CategoryExpense.Groceries.name  -> return R.drawable.groceries
        CategoryExpense.Household.name  -> return R.drawable.household

        CategoryExpense.Internet.name   -> return R.drawable.internet
        CategoryExpense.Gym.name        -> return R.drawable.gym
        CategoryExpense.Medical.name    -> return R.drawable.medical

        CategoryExpense.Investment.name -> return R.drawable.investment
        CategoryExpense.Food.name       -> return R.drawable.food
        CategoryExpense.Utilities.name  -> return R.drawable.utilities

        CategoryExpense.Shopping.name   -> return R.drawable.shopping
        CategoryExpense.Movies.name     -> return R.drawable.movie
        CategoryExpense.Charity.name    -> return R.drawable.charity
        CategoryExpense.Other.name     -> return R.drawable.expense

        CategoryIncome.Salary.name      -> return R.drawable.salary
        CategoryIncome.Stocks.name      -> return R.drawable.stock
        CategoryIncome.Freelance.name   -> return R.drawable.freelance
        CategoryIncome.Others.name      -> return R.drawable.income
    }

    return R.drawable.others
}

fun getMonthList() : Array<String> {

    return arrayOf("May 2020", "June 2020", "July 2020", "Aug 2020", "Sept 2020",
        "Oct 2020", "Nov 2020", "Dec 2020", "Jan 2021", "Feb 2021", "March 2021", "April 2021")

}