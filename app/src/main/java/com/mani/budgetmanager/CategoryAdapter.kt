package com.mani.budgetmanager

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mani.budgetmanager.CommonFiles.getDrawableImageId
import kotlinx.android.synthetic.main.dialog_expense_income.*

class CategoryAdapter(
    private val mCtx: Context,
    private val mList: MutableList<Category>,
    private val dialog: Dialog) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var lastCheckedView : LinearLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return  CategoryViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_category, parent, false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        var category = mList[position]

        if(lastCheckedView==null){
            lastCheckedView = holder.container
            holder.container.alpha = 1.0F
        }


        holder.container.setOnClickListener {

            if(lastCheckedView!=null)
                lastCheckedView!!.alpha = .2f

            lastCheckedView = holder.container
            holder.container.alpha = 1.0F

            dialog.category_name.text = ""+category.name
            dialog.title.setText(category.name)
            dialog.title.setSelection(dialog.title.text.length)

        }

        var imageId = getDrawableImageId(category.name)

        holder.icon.setImageDrawable(ContextCompat.getDrawable(mCtx,imageId))
        holder.name.text = category.name



    }


    class CategoryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val container :LinearLayout = itemView.findViewById(R.id.ll_container)
        val icon:ImageView = itemView.findViewById(R.id.icon)
        val name:TextView = itemView.findViewById(R.id.name)

    }
}