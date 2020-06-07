package com.mani.budgetmanager.Income

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mani.budgetmanager.CommonFiles.SharedPreference
import com.mani.budgetmanager.CommonFiles.formatMoney
import com.mani.budgetmanager.CommonFiles.getDrawableImageId
import com.mani.budgetmanager.R
import com.mani.budgetmanager.Room.AppDatabase


// following 5 task has to be done after removing income

//1. Remove income detail from Room income table
//2. Update total income in shared pref
//3. update budget ui in mainActivity
//4. Update recycler view income as well ad
//5  Update recycler view expense ( since % expence depend upon income)

// better combine 3rd, 4th and 5th task => setupViewPager() in mainActivity

class IncomeAdapter(
    private val mCtx: Context,
    private val mExpenseList: MutableList<IncomeModel>,
    private val fragIncome: FragIncome

) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        return IncomeViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_income,parent,false))
    }

    override fun getItemCount(): Int {
        return mExpenseList.size
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val incomeModel = mExpenseList[position]

        holder.icon.setImageDrawable(ContextCompat.getDrawable(mCtx, getDrawableImageId(incomeModel.category)))

        holder.title.text = incomeModel.title
        holder.amount.text = formatMoney(incomeModel.amount)

        holder.remove.setOnClickListener {
            openConfirmationDialog(incomeModel)
        }
    }

    class IncomeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val icon    : ImageView= itemView.findViewById(R.id.category_icon)
        val title   : TextView = itemView.findViewById(R.id.title)
        val amount  : TextView = itemView.findViewById(R.id.amount)

        var remove : ImageView = itemView.findViewById(R.id.remove)
    }


    private fun openConfirmationDialog(incomeModel: IncomeModel) {

        val alertDialog = AlertDialog.Builder(mCtx)

        alertDialog.setTitle("Remove Income")
        alertDialog.setMessage("Do you really want to remove this income?")
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert)

        alertDialog.setPositiveButton(android.R.string.yes) { dialog, _ ->
            RemoveFromIncomeTable(AppDatabase.getInstance(mCtx)!!).execute(incomeModel)
        }

        alertDialog.setNegativeButton(android.R.string.no, null)

        alertDialog.show()

    }

    inner class RemoveFromIncomeTable(instance: AppDatabase) : AsyncTask<IncomeModel, Void, Void>() {

        private var incomeDao = instance.incomeDao
        private lateinit var income : IncomeModel

        override fun doInBackground(vararg params: IncomeModel): Void? {
            incomeDao.removeById(params[0].id)                              //Task 1 : done
            income = params[0]
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            fragIncome.updateSharedPrefAndStartViewPager(false,income.amount)
        }
    }
}