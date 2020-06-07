package com.mani.budgetmanager.Income


import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mani.budgetmanager.Category
import com.mani.budgetmanager.CategoryAdapter
import com.mani.budgetmanager.CommonFiles.AppConstants.Companion.CategoryIncome
import com.mani.budgetmanager.CommonFiles.SharedPreference
import com.mani.budgetmanager.CommonFiles.getMonthList
import com.mani.budgetmanager.CommonFiles.showToast
import com.mani.budgetmanager.MainActivity
import com.mani.budgetmanager.R
import com.mani.budgetmanager.Room.AppDatabase
import kotlinx.android.synthetic.main.dialog_expense_income.*
import kotlinx.android.synthetic.main.fragment.*
import kotlinx.android.synthetic.main.fragment.view.*
import kotlinx.android.synthetic.main.fragment.view.add_btn
import kotlinx.android.synthetic.main.fragment.view.blank_layout

// following 5 task has to be done after adding income

//1. Add income detail to Room income table
//2. Update total income in shared pref
//3. update budget ui in mainActivity
//4. Update recycler view income as well ad
//5  Update recycler view expense ( since % expence depend upon income)

// better combine 3rd, 4th and 5th task => setupViewPager() in mainActivity

class FragIncome : Fragment() {

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
        getIncomeListAndSetAdapter()

        return mRootView
    }


    private fun setupClickListener() {
        mRootView.add_btn.setImageDrawable(ContextCompat.getDrawable(mCtx, R.drawable.plus))
        mRootView.add_btn.setOnClickListener { openIncomeDialog() }
    }

    private fun openIncomeDialog() {

        val dialog = Dialog(mCtx)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_expense_income)
        setupIncomeCategoryDialog(dialog)

        dialog.title.setText(CategoryIncome.Salary.name)
        dialog.category_name.text = CategoryIncome.Salary.name
        dialog.title.setSelection(dialog.title.text.length)

        dialog.save.setOnClickListener{

            var income : IncomeModel? =  getIncomeData(dialog)

            if(income!=null){

                AddToIncomeTable(AppDatabase.getInstance(mCtx)!!).execute(income)       //Task 1
                dialog.dismiss()
            }
        }

        dialog.show()
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCtx = context
    }


    private fun getIncomeListAndSetAdapter(){
        GetIncomeList(AppDatabase.getInstance(mCtx)!!).execute()
    }

    private fun getIncomeData(dialog: Dialog): IncomeModel? {

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
            return IncomeModel(0,monthSelected, title, amount.toInt(), category)
        }

    }


    private fun setupIncomeCategoryDialog(dialog: Dialog) {

        var categoryList = ArrayList<Category>()

        categoryList.add(Category(1, CategoryIncome.Salary.name))
        categoryList.add(Category(2, CategoryIncome.Stocks.name))
        categoryList.add(Category(3, CategoryIncome.Freelance.name))
        categoryList.add(Category(4, CategoryIncome.Others.name))


        dialog.recycler_view_category.layoutManager = LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CategoryAdapter(mCtx, categoryList, dialog)
        dialog.recycler_view_category.adapter = adapter

    }

    private fun setAdapter(list: MutableList<IncomeModel>) {

        if(list.size==0)
            blank_layout.visibility = View.VISIBLE
        else
            blank_layout.visibility = View.GONE

        mRootView.recycler_view.layoutManager = LinearLayoutManager(mCtx)
        list.reverse()
        var adapter = IncomeAdapter(mCtx, list, this)
        mRootView.recycler_view.adapter = adapter

    }


    inner class AddToIncomeTable(instance: AppDatabase) : AsyncTask<IncomeModel, Void, Void>() {

        private var incomeDao = instance.incomeDao
        private lateinit var income : IncomeModel

        override fun doInBackground(vararg params: IncomeModel): Void? {
            incomeDao.insert(params[0])                                     // Task 1 : done
            income = params[0]
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            mCtx.showToast("Income successfully added")

            updateSharedPrefAndStartViewPager(true, income.amount)

        }
    }

    inner class GetIncomeList(instance: AppDatabase) : AsyncTask<Void, Void, Void>() {

        private var incomeDao = instance.incomeDao
        var incomeList : MutableList<IncomeModel>? = null

        var monthSelected = mSharedPref.getCurrentMonth()

        override fun doInBackground(vararg params: Void?): Void? {
            incomeList = incomeDao.getIncomeByMonth(monthSelected)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if(incomeList!=null)
                setAdapter(incomeList!!)
        }
    }



    fun updateSharedPrefAndStartViewPager(amountToAdd: Boolean, amount: Int) {
        mSharedPref.updateTotalIncome(amountToAdd,amount)      //Task 2 : done
        mActivity.setupViewPager(1)                          //Task 2,4,5 : done

    }
}
