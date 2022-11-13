package com.msa.myexpenses.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.msa.myexpenses.R
import com.msa.myexpenses.adapters.MyExpensesAdapter
import com.msa.myexpenses.databinding.FragmentBottomDialogBinding
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.room_db.database.DatabaseClient
import java.util.*
import kotlin.math.exp

class BottomDialogFragment(var onExpenseListener : onExpensesListener) : BottomSheetDialogFragment() {
    // الinterface عشان امرر قيم اللي بدي اضيفها او أعدل عليها عشان تصل للactivity واأقدر أعدل على الadapter مباشرة
    interface onExpensesListener{
        fun addExpense(expense : Expenses)
        fun editExpenses(expense : Expenses)
    }

    private lateinit var dialogBinding: FragmentBottomDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogBinding = FragmentBottomDialogBinding.inflate(inflater, container, false)

        val sharedPref = requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)

        // إما أعرض تاريخ اليوم في حالة الاضافة أو اعرض التاريخ المحفوظ قبل في حالة التعديل
        if (Expenses.type == "editExpense") {
            dialogBinding.tvAddOrEdit.text = "Edit Expense"
            // بدي اعدل على قيمة هتكون وصلتني قيم الobject اللي هعدل عليه من الadapter.. بدي اعرض القيم داخل الfields
            val editPlace = sharedPref.getString("place", "").toString()
            val editMoney = sharedPref.getFloat("money", (-1.0).toFloat())
            val editDate = sharedPref.getString("date", "").toString()

            dialogBinding.etPlace.setText(editPlace)
            dialogBinding.etMoney.setText(editMoney.toString())
            dialogBinding.etDate.setText(editDate)
        } else {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH)
            val year = currentDate.get(Calendar.YEAR)

            val displayDay = if(day < 10) "0$day" else "$day"
            val displayMonth = if(month + 1 < 10) "0${month + 1}" else "${month + 1}"
            dialogBinding.etDate.setText("$year/$displayMonth/$displayDay")
        }

        var day = 0; var month = 0; var year = 0
        if (Expenses.type == "editExpense") {
            // عشان في حالة تعديل التاريخ يفتح الdialog على نفس التاريخ اللي كان بالاول
            val editDate = sharedPref.getString("date", "").toString()
            val first = editDate.indexOf("/")
            val last = editDate.lastIndexOf("/")
            year = editDate.substring(0, first).toInt()
            month = editDate.substring(first + 1, last).toInt() - 1
            day = editDate.substring(last + 1).toInt()
        }else{
            val currentDate = Calendar.getInstance()
            day = currentDate.get(Calendar.DAY_OF_MONTH)
            month = currentDate.get(Calendar.MONTH)
            year = currentDate.get(Calendar.YEAR)
        }

        dialogBinding.etDate.setOnClickListener {
            val picker = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                val displayDay = if(d < 10) "0$d" else "$d"
                val displayMonth = if(m + 1 < 10) "0${m + 1}" else "${m + 1}"
                dialogBinding.etDate.setText("$y/$displayMonth/$displayDay")
                }, year, month, day
            )
            picker!!.show()
        }

        dialogBinding.btnSave.setOnClickListener {
            val place = dialogBinding.etPlace.text.toString()
            val money = dialogBinding.etMoney.text.toString()
            val date = dialogBinding.etDate.text.toString()

            if (place.isEmpty() || money.isEmpty() || date.isEmpty()) {
                Toast.makeText(activity, R.string.fillFields, Toast.LENGTH_SHORT).show()
            } else {
                if (Expenses.type == "newExpense") {
                    try {
                        val moneyWriten = money.toDouble()
                        if(moneyWriten > 0) {
                            val sharedEmail = requireActivity().getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
                            val email = sharedEmail.getString("email", "").toString()

                            // بدون ID لانو بضيف تلقائي
                            val expense = Expenses()
                            expense.userEmail = email
                            expense.place = place
                            expense.money = money.toDouble()
                            expense.date = date

                            // بمرر الobject للfunction عشان اوصله للactivity واحدث مباشرة على الشاشة
                            onExpenseListener.addExpense(expense)
                            DatabaseClient.getInstance(activity)!!.appDatabase.expenseDao()
                                .insertExpense(expense)

                            Toast.makeText(activity, R.string.addExpense, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }else{
                            dialogBinding.etMoney.setError(R.string.lessThan0.toString())
                        }
                    } catch (ex: Exception) {
                        dialogBinding.etMoney.setError(R.string.correctNumber.toString())
                    }
                } else if (Expenses.type == "editExpense") {
                    try {
                        val moneyWriten = money.toDouble()
                        if(moneyWriten > 0) {
                            val sharedEmail = requireActivity().getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
                            val email = sharedEmail.getString("email", "").toString()

                            val Id = sharedPref.getInt("Id", 0)

                            val editExpense = Expenses()
                            editExpense.e_id = Id
                            editExpense.userEmail = email
                            editExpense.place = place
                            editExpense.money = money.toDouble()
                            editExpense.date = date

                            // البيانات الموجودة بالسطر اللي تحت هي البيانات الجديدة المحدثة
                            onExpenseListener.editExpenses(editExpense)

                            DatabaseClient.getInstance(activity)!!.appDatabase.expenseDao()
                                .editExpense(editExpense)
                            Toast.makeText(activity, R.string.updateExpense, Toast.LENGTH_SHORT)
                                .show()
                            dismiss()
                        }else{
                            dialogBinding.etMoney.setError(R.string.lessThan0.toString())
                        }
                    } catch (ex: Exception) {
                        dialogBinding.etMoney.setError(R.string.correctNumber.toString())
                    }
                }
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        return dialogBinding.root
    }

}