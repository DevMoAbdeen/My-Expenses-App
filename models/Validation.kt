package com.msa.myexpenses.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Validation(
    @PrimaryKey var email: String,
    @ColumnInfo(name = "birthdate") var birthdate: String,
    @ColumnInfo(name = "animal") var animal: String,
    @ColumnInfo(name = "color") var color: String,
    @ColumnInfo(name = "drink") var drink: String
){
    companion object{
        var type = "answerQuestions"
    }
}
