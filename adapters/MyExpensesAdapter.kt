package com.msa.myexpenses.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.msa.myexpenses.R
import com.msa.myexpenses.activities.HomeActivity
import com.msa.myexpenses.databinding.RvListExpensesBinding
import com.msa.myexpenses.fragments.BottomDialogFragment
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.room_db.database.DatabaseClient

class MyExpensesAdapter(var activity: Activity, var data: MutableList<Expenses>, var onExpenseListener : OnExpensesListener ):
    RecyclerView.Adapter<MyExpensesAdapter.ExpenseViewHolder>(){

    // بعمل interface وبمرر له position العنصر اللي بدي احذفه عشان اوصل الposition للHome واحذف العنصر من الArray اللي بعرض منها مباشرة..
    // عشان لو كان اخر عنصر يعرض الصورة وجملة No Expenses
    interface OnExpensesListener{
        fun deleteExpenses(position : Int)
    }

    inner class ExpenseViewHolder(var binding: RvListExpensesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder(RvListExpensesBinding.inflate(LayoutInflater.from(activity), parent, false))
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.binding.rvPlace.text = data[holder.adapterPosition].place
        holder.binding.rvData.text = data[holder.adapterPosition].date
        // اذا الرقم فيه فاصلة عشرية بكتبه كامل واذا ما فيه بحوله لinteger
        if(data[holder.adapterPosition].money.toString().endsWith(".0")){
            val len = data[holder.adapterPosition].money.toString().length
            val money = data[holder.adapterPosition].money.toString().substring(0, len - 2)
            holder.binding.rvPrice.text = money
        }else{
            holder.binding.rvPrice.text = data[holder.adapterPosition].money.toString()
        }


        holder.binding.rvDelete.setOnClickListener {
            val alert = AlertDialog.Builder(activity)
            alert.setTitle(R.string.isDeleteExpense)
            alert.setIcon(R.drawable.ic_delete_black)
            alert.setMessage(R.string.sureDeleteExpense)

            alert.setPositiveButton(R.string.yes) { d, i ->
                DatabaseClient.getInstance(activity)!!.appDatabase.expenseDao().deleteExpense(data[position])
                // بعطي الposition لل function عشان اوصل الposition للactivity واقدر احذف العنصر من الarray
                onExpenseListener.deleteExpenses(position)
                notifyDataSetChanged()
                Toast.makeText(activity,R.string.deleteExpense, Toast.LENGTH_SHORT).show()
            }

            alert.setNegativeButton(R.string.no) { d, i ->
                d.cancel()
            }
            alert.show()
        }

        holder.binding.rvEdit.setOnClickListener {
            Expenses.type = "editExpense"

            // بعمل object من الacticity عشان اقدر اصل لsupportFragmentManager
            val home = activity as HomeActivity
            val bottomDialog = BottomDialogFragment(object: BottomDialogFragment.onExpensesListener{
                override fun addExpense(expense: Expenses) {}

                override fun editExpenses(expense: Expenses) {
                    // بعطي الobject للfunction عشان اوصله للactivity واعدل عليه مباشرة على adapter
                    data[holder.adapterPosition] = expense
                    notifyDataSetChanged()
                }

            })
            // برسل البيانات عشان اعرضهم بالform ويقدر المستخدم يعدل على القيم اللي بده اياها
                    val sharedPref = activity.getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
                    val expense = sharedPref.edit()
                    expense.putInt("Id", data[holder.adapterPosition].e_id)
                    expense.putString("place", data[holder.adapterPosition].place)
                    expense.putString("date", data[holder.adapterPosition].date)
                    expense.putFloat("money", data[holder.adapterPosition].money.toFloat())
                    expense.apply()

            bottomDialog.show(home.supportFragmentManager, "editExpense")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

