package com.mani.budgetmanager.Expense

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mani.budgetmanager.CommonFiles.AppConstants.Companion.CategoryExpense
import com.mani.budgetmanager.CommonFiles.SharedPreference
import com.mani.budgetmanager.CommonFiles.formatMoney
import com.mani.budgetmanager.CommonFiles.getDrawableImageId
import com.mani.budgetmanager.R
import com.mani.budgetmanager.Room.AppDatabase
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round


// following 4 task has to be done after removing expense

//1. Remove expense detail to Room Expense table
//2. Update total expense in shared pref
//3. update budget ui in mainActivity
//4. Update recycler view

class ExpenseAdapter(
    private val mCtx: Context,
    private val mExpenseList: MutableList<ExpenseModel>,
    private val fragExpense: FragExpense
)
    : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>(){


    val mSharedPref = SharedPreference(mCtx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_expense,parent,false))
    }

    override fun getItemCount(): Int {
        return mExpenseList.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

        var expense = mExpenseList[position]

        holder.title.text = expense.title
        holder.amount.text = formatMoney(expense.amount)

        var iconId = getDrawableImageId(expense.category)
        holder.icon.setImageDrawable(ContextCompat.getDrawable(mCtx, iconId))

        var expPer = getExpensePercentage(expense.amount)

        if(expPer!=-1.0) {
            holder.exp_per.text = "$expPer%"
            holder.exp_progress.progress = expPer.toInt()
        }
        else{
            holder.exp_per.text = "Infinity"
            holder.exp_progress.progress = 100
        }

        holder.remove.setOnClickListener {
            openConfirmationDialog(expense)
        }

    }


    private fun getExpensePercentage(expenseAmount: Int) : Double{

        var totalIncome = mSharedPref.getTotalIncome().toFloat()

        if(totalIncome!=0F){

            var ex = (expenseAmount/totalIncome)*100.0
            return roundOffDecimal(ex)
        }
        else{
            return -1.0
        }

    }

    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }


    class ExpenseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val icon    : ImageView    = itemView.findViewById(R.id.category_icon)
        val title   : TextView     = itemView.findViewById(R.id.title)
        val amount  : TextView     = itemView.findViewById(R.id.amount)
        val exp_per : TextView     = itemView.findViewById(R.id.exp_percentage)
        val exp_progress : ProgressBar  = itemView.findViewById(R.id.exp_progress)

        val remove : ImageView = itemView.findViewById(R.id.remove)
    }


    private fun openConfirmationDialog(expense: ExpenseModel) {

        val alertDialog = AlertDialog.Builder(mCtx)

        alertDialog.setTitle("Remove Expense")
        alertDialog.setMessage("Do you really want to remove this expense?")
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert)

        alertDialog.setPositiveButton(android.R.string.yes) { dialog, _ ->

            // following 4 task has to be done after removing expense
            RemoveFromExpenseTable(AppDatabase.getInstance(mCtx)!!).execute(expense)
        }

        alertDialog.setNegativeButton(android.R.string.no, null)
        alertDialog.show()

    }




    inner class RemoveFromExpenseTable(instance: AppDatabase) : AsyncTask<ExpenseModel, Void, Void>() {

        private var expenseDao = instance.expenseDao
        private lateinit var expense : ExpenseModel

        override fun doInBackground(vararg params: ExpenseModel): Void? {

            expenseDao.removeById(params[0].id)                      //Task 1 : done
            expense = params[0]
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            fragExpense.updateSharedPrefAndExpenseUi(false, expense.amount)      //Task 2,3,4
        }
    }
}