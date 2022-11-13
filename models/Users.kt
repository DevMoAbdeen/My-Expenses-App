package com.msa.myexpenses.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Users{
     @PrimaryKey(autoGenerate = true) var Id: Int = 0
     @ColumnInfo(name = "user_name") var name: String? = null
//     @ColumnInfo(name = "user_mobile") var mobile: String? = null
     @ColumnInfo(name = "user_email") var email: String? = null
     @ColumnInfo(name = "user_password") var password: String? = null
}