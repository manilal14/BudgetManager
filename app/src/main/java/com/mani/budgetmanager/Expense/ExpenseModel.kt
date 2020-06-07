package com.mani.budgetmanager.Expense

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ExpenseModel (

    @PrimaryKey (autoGenerate = true)
    var id: Int,
    var month : String,
    var title:String,
    var amount :Int,
    var category:String
)
