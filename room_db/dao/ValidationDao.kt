package com.msa.myexpenses.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.models.Validation

@Dao
interface ValidationDao {

    @Insert
    fun insertValidation(validation: Validation)

    @Query("Select * From Validation Where email = :email")
    fun getAnswers(email: String): MutableList<Validation>

    @Query("Delete From Validation Where email = :email")
    fun deleteValidationUser(email: String)

    @Query("Delete From Validation")
    fun deleteAllValidation()

}