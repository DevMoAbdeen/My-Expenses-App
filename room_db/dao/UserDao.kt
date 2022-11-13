package com.msa.myexpenses.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.models.Users

@Dao
interface UserDao {
    @Insert
    fun insertUser(user:Users)
//    fun insertUser(name:String, mobile:String, email:String, password:String)

    @Update
    fun editUser(user: Users)

    @Query("Select * From Users Where user_email = :email And user_password =:password")
    fun login(email: String, password:String) : MutableList<Users>

    @Query("Select * From Users Where user_email = :email")
    fun isExist(email:String) : MutableList<Users>

    @Query("Select user_password From Users Where user_email = :email")
    fun getPassword(email:String) : String

    @Query("Delete From Users Where user_email = :email")
    fun deleteUser(email: String)

    @Query("Delete From Users")
    fun deleteAllUsers()

}