package com.mani.budgetmanager.Income

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class IncomeModel (

    @PrimaryKey (autoGenerate = true)
    var id     :Int,
    var month : String,
    var title  :String,
    var amount :Int,
    var category:String
)