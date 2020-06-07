package com.mani.budgetmanager.Expense

import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mani.budgetmanager.*
import com.mani.budgetmanager.CommonFiles.AppConstants.Companion.CategoryExpense
import com.mani.budgetmanager.CommonFiles.SharedPreference
import com.mani.budgetmanager.CommonFiles.getMonthList
import com.mani.budgetmanager.CommonFiles.showToast
import com.mani.budgetmanager.Room.AppDatabase
import kotlinx.android.synthetic.main.dialog_expense_income.*
import kotlinx.android.synthetic.main.fragment.*
import kotlinx.android.synthetic.main.fragment.view.*
import kotlinx.android.synthetic.main.fragment.view.blank_layout
import kotlin.collections.ArrayList


// following 4 task has to be done after adding expense

//1. Add expense detail to Room Expense table
//2. Update total expense in shared pref
//3. update budget ui in mainActivity
//4. Update recycler view of ExpenseList


class FragExpense : Fragment() {

    private val TAG = this::class.java.simpleName
    private lateinit var  mRootView : View
    private lateinit var mCtx : Context

    private lateinit var mSharedPref : SharedPreference

    private lateinit var mActivity : MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mRootView =  inflater.inflate(R.layout.fragment, container, false)
        mSharedPref = SharedPreference(mCtx)

        setupClickListener()
        getExpenseListAndSetAdapter()

        return mRootView
    }


    private fun getExpenseListAndSetAdapter(){
        GetExpenseList(AppDatabase.getInstance(mCtx)!!).execute()
    }

    private fun setupClickListener() {
        mRootView.add_btn.setImageDrawable(ContextCompat.getDrawable(mCtx, R.drawable.minus))
        mRootView.add_btn.setOnClickListener { openExpenseDialog() }
    }

    private fun openExpenseDialog() {

        val dialog = Dialog(mCtx)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_expense_income)

        setUpCategoryDialog(dialog)

        dialog.title.setText(CategoryExpense.Rent.name)
        dialog.category_name.text = CategoryExpense.Rent.name
        dialog.title.setSelection(dialog.title.text.length)

        dialog.save.setOnClickListener{

            var expense : ExpenseModel? =  getExpenseData(dialog)

            if(expense!=null){

                // following 4 task has to be done after adding expense
                AddToExpense(AppDatabase.getInstance(mCtx)!!).execute(expense)
                dialog.dismiss()
            }
        }

        dialog.show()
    }











    private fun getExpenseData(dialog: Dialog): ExpenseModel? {

        var title    = dialog.title.text.toString()
        var amount   = dialog.amount.text.toString()
        var category = dialog.category_name.text.toString()

        var monthSelected = mSharedPref.getCurrentMonth()

        Log.e("1", monthSelected)


        if(title.isEmpty() || amount.isEmpty()){
            mCtx.showToast("All fields are required")
            return null
        }
        else {
            return ExpenseModel(0, monthSelected, title, amount.toInt(), category)
        }

    }


    private fun setUpCategoryDialog(dialog: Dialog) {

        var categoryList = getCategoryList()

        dialog.recycler_view_category.layoutManager = LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CategoryAdapter(mCtx, categoryList, dialog)
        dialog.recycler_view_category.adapter = adapter

    }

    private fun getCategoryList(): MutableList<Category> {

        var categoryList = ArrayList<Category>()

        categoryList.add(Category(1,  CategoryExpense.Rent.name))
        categoryList.add(Category(2,  CategoryExpense.Groceries.name))
        categoryList.add(Category(3,  CategoryExpense.Household.name))
        categoryList.add(Category(4,  CategoryExpense.Internet.name))
        categoryList.add(Category(5,  CategoryExpense.Gym.name))
        categoryList.add(Category(6,  CategoryExpense.Medical.name))
        categoryList.add(Category(7,  CategoryExpense.Investment.name))
        categoryList.add(Category(8,  CategoryExpense.Food.name))
        categoryList.add(Category(9,  CategoryExpense.Utilities.name))
        categoryList.add(Category(10,  CategoryExpense.Shopping.name))
        categoryList.add(Category(11,  CategoryExpense.Movies.name))
        categoryList.add(Category(12,  CategoryExpense.Charity.name))
        categoryList.add(Category(13,  CategoryExpense.Other.name))

        return categoryList

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCtx = context
    }


    private fun setAdapter(expenseList: MutableList<ExpenseModel>) {

        if(expenseList.size==0)
            blank_layout.visibility = VISIBLE
        else
            blank_layout.visibility = GONE

        mRootView.recycler_view.layoutManager = LinearLayoutManager(mCtx)
        expenseList.reverse()
        var adapter   = ExpenseAdapter(mCtx, expenseList, this)
        mRootView.recycler_view.adapter = adapter

    }




    /*-------------------------classes related to RoomDatabase-----------------------*/

    inner class AddToExpense(instance: AppDatabase) : AsyncTask<ExpenseModel, Void, Void>() {

        private var expenseDao = instance.expenseDao
        private lateinit var expense : ExpenseModel

        override fun doInBackground(vararg params: ExpenseModel): Void? {
            expenseDao.insert(params[0])                                    //Task 1 : done
            expense = params[0]
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            mCtx.showToast("Expense successfully added")
            updateSharedPrefAndExpenseUi(true, expense.amount)                                                 //Task 3,4

        }
    }

    inner class GetExpenseList(instance: AppDatabase) : AsyncTask<Void, Void, Void>() {

        private var expenseDao = instance.expenseDao
        private var expenseList : MutableList<ExpenseModel>? = null

        var monthSelected = mSharedPref.getCurrentMonth()

        override fun doInBackground(vararg params: Void?): Void? {
            expenseList = expenseDao.getExpenseByMonth(monthSelected)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if(expenseList!=null)
                setAdapter(expenseList!!)
        }
    }


    fun updateSharedPrefAndExpenseUi(amountToAdd: Boolean, amount: Int){

        mSharedPref.updateTotalExpense(amountToAdd,amount)      //Task 2 : done
        mActivity.updateBudgetUi()                           //Task 3 : done
        getExpenseListAndSetAdapter()                        //Task 4 : done

    }


}
