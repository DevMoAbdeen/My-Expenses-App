package com.msa.myexpenses.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//@Entity(foreignKeys = @ForeignKey(entity = Users, parentColumns = "Id", childColumns = "expense_userID", onDelete = CASCADE))
@Entity
class Expenses{
    @PrimaryKey(autoGenerate = true) var e_id:Int = 0
    @ColumnInfo(name = "expense_userEmail") var userEmail:String? = null
    @ColumnInfo(name = "expense_place") var place:String? = null
    @ColumnInfo(name = "expense_price") var money:Double = 0.0
    @ColumnInfo(name = "expense_date") var date:String? = null

    companion object{
        var type = "newExpense"
    }
}
