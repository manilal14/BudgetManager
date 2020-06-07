package com.mani.budgetmanager.CommonFiles

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(ctx : Context){

    private val PRIVATE_MODE = 0
    private val PREF_NAME = "LoginPreference"

    private val mCtx   : Context = ctx
    private var pref   : SharedPreferences
    private val editor : SharedPreferences.Editor


    init {
        pref    =  mCtx.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
        editor  = pref.edit()
    }

    companion object{

        private val TOTAL_EXPENSE = "totalExpense"
        private val TOTAL_INCOME  = "totalIncome"

        private val SELECTED_MONTH_INDEX = "selected_month_index"
    }


    fun updateTotalExpense(amountToAdd : Boolean, amount : Int){

        var currentExpense = getTotalExpence()

        if(amountToAdd)
            currentExpense += amount
        else
            currentExpense -= amount

        editor.putInt(TOTAL_EXPENSE, currentExpense)
        editor.commit()
    }

    fun updateTotalIncome(amountToAdd : Boolean , amount : Int){

        var currentIncome = getTotalIncome()

        if(amountToAdd)
            currentIncome += amount
        else
            currentIncome -= amount

        editor.putInt(TOTAL_INCOME, currentIncome)
        editor.commit()
    }

    fun getTotalExpence(): Int {
        return pref.getInt(TOTAL_EXPENSE, 0)
    }

    fun getTotalIncome(): Int {
        return pref.getInt(TOTAL_INCOME, 0)
    }

    fun resetTotalIncomeAndExpense(totalIncome : Int, totalExpense : Int){
        editor.putInt(TOTAL_INCOME, totalIncome)
        editor.putInt(TOTAL_EXPENSE, totalExpense)
        editor.commit()
    }

    fun resetIncomeExpenseToZero(){
        editor.putInt(TOTAL_INCOME, 0)
        editor.putInt(TOTAL_EXPENSE, 0)
        editor.commit()
    }


    fun getSelectedMonthIndex() : Int {
        return pref.getInt(SELECTED_MONTH_INDEX, 0)
    }

    fun getCurrentMonth() : String{

        var index = getSelectedMonthIndex()
        return getMonthList()[index]
    }

    fun setSelectedMonthIndex(monthIndex:Int){
        editor.putInt(SELECTED_MONTH_INDEX, monthIndex)
        editor.commit()
    }





}