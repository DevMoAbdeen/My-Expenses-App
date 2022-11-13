package com.msa.myexpenses.room_db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.models.Users
import com.msa.myexpenses.models.Validation
import com.msa.myexpenses.room_db.dao.ExpenseDao
import com.msa.myexpenses.room_db.dao.UserDao
import com.msa.myexpenses.room_db.dao.ValidationDao

@Database(entities = [Users::class, Expenses::class, Validation::class], version = 1)
abstract class AppDatabase :RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun expenseDao() : ExpenseDao
    abstract fun validationDao() : ValidationDao
}