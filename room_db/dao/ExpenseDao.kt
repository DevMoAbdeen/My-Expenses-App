package com.msa.myexpenses.room_db.dao

import androidx.room.*
import com.msa.myexpenses.models.Expenses

@Dao
interface ExpenseDao {

    @Insert
    fun insertExpense(expense: Expenses)
//    @Query("Insert into Expenses Values(:id, :email, :place, :money, :date)")
//    fun insertExpense(id:Int, email:String, place:String, money:Double, date:String)

    @Update
    fun editExpense(expense: Expenses)

    @Delete
    fun deleteExpense(expense: Expenses)

    @Query("Select * From Expenses Where expense_userEmail = :userEmail Order By expense_date DESC")
    fun getAllExpenses(userEmail: String) : MutableList<Expenses>

    @Query("Select * From Expenses Where expense_userEmail = :email And expense_date = :date Order By expense_date DESC")
    fun searchExpenses(email: String, date: String) : MutableList<Expenses>

    @Query("Delete From Expenses Where expense_userEmail = :email")
    fun deleteExpensesUser(email: String)

    @Query("Delete From Expenses")
    fun deleteAllExpenses()

}