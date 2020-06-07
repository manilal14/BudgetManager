package com.mani.budgetmanager

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mani.budgetmanager.CommonFiles.SharedPreference
import com.mani.budgetmanager.CommonFiles.formatMoney
import com.mani.budgetmanager.CommonFiles.getMonthList
import com.mani.budgetmanager.Expense.ExpenseModel
import com.mani.budgetmanager.Expense.FragExpense
import com.mani.budgetmanager.Income.FragIncome
import com.mani.budgetmanager.Income.IncomeModel
import com.mani.budgetmanager.Room.AppDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    private lateinit var  mSharedPref : SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSharedPref = SharedPreference(this)

        setMonthSpinner()
        clickListener()
        setupViewPager()
    }

    private fun setMonthSpinner() {

        val items = getMonthList()
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.spinner_item, items)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner_month.adapter = adapter

        var month = spinner_month.selectedItem.toString()
        Log.e(TAG, month)


        spinner_month.setSelection(mSharedPref.getSelectedMonthIndex())

        spinner_month.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {

                mSharedPref.setSelectedMonthIndex(position)

                GetMonthlyTotalIncomeExpense(AppDatabase.getInstance(this@MainActivity)!!).execute()

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }


    }

    private fun clickListener() {

        three_dots.setOnClickListener {
            if (menu_view.visibility==VISIBLE)
                menu_view.visibility = GONE
            else
                menu_view.visibility = VISIBLE
        }

        clear_all.setOnClickListener {
            menu_view.visibility = GONE
            openConfirmationDialog()
        }

        layout_main_activity.setOnClickListener {
            menu_view.visibility = GONE
        }









    }

    private fun openConfirmationDialog() {

        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("Delete All Data")
        alertDialog.setMessage("Do you really want to delete all the data?")
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert)

        alertDialog.setPositiveButton(android.R.string.yes) { _,_ ->
            ClearCurrentMonthData(AppDatabase.getInstance(this)!!).execute()
        }

        alertDialog.setNegativeButton(android.R.string.no, null)

        alertDialog.show()
    }


    fun updateBudgetUi() {

        val sharedPref = SharedPreference(this)

        var income    = sharedPref.getTotalIncome()
        var expence   = sharedPref.getTotalExpence()
        var moneyRem  = income - expence

        var expPer = (expence.toFloat()/income)*100.0

        total_exp_progress.progress = expPer.toInt()


        total_expense.text = formatMoney(expence)
        total_income.text  = formatMoney(income)
        money_left.text    = formatMoney(moneyRem)

        if(moneyRem<=0)
            money_left.setTextColor(ContextCompat.getColor(this, R.color.red))
        else
            money_left.setTextColor(ContextCompat.getColor(this, R.color.green))

        
    }


    fun setupViewPager(tabIndex : Int = 0) {

        updateBudgetUi()

        var viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(FragExpense(),"Expense")
        viewPagerAdapter.addFragment(FragIncome(),"Income")

        view_pager.adapter = viewPagerAdapter

        tabs.setupWithViewPager(view_pager)
        tabs.getTabAt(tabIndex)!!.select()

    }



    inner class ClearCurrentMonthData(instance: AppDatabase) : AsyncTask<Void, Void, Void>() {

        private var incomeDao = instance.incomeDao
        private var expenseDao = instance.expenseDao

        var monthSelected = mSharedPref.getCurrentMonth()


        override fun doInBackground(vararg params: Void?): Void? {
            incomeDao.deleteCurrentMonthIncome(monthSelected)
            expenseDao.deleteCurrentMonthExpemse(monthSelected)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            SharedPreference(this@MainActivity).resetIncomeExpenseToZero()
            setupViewPager()
        }
    }


    inner class GetMonthlyTotalIncomeExpense(instance: AppDatabase) : AsyncTask<Void, Void, Void>() {

        private var incomeDao = instance.incomeDao
        private var expenseDao = instance.expenseDao

        private lateinit var incomeList  : List<Int>
        private lateinit var expenseList : List<Int>

        var monthSelected = mSharedPref.getCurrentMonth()

        override fun doInBackground(vararg params: Void?): Void? {
            incomeList = incomeDao.getTotalMonthlyIncome(monthSelected)
            expenseList = expenseDao.getTotalMonthlyExpense(monthSelected)

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            var totalIncome = incomeList.sum()
            var totalExpense = expenseList.sum()

            mSharedPref.resetTotalIncomeAndExpense(totalIncome, totalExpense)

            setupViewPager()

        }
    }




}
