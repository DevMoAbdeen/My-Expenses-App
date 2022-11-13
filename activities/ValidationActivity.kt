package com.msa.myexpenses.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.msa.myexpenses.R
import com.msa.myexpenses.databinding.ActivityHomeBinding
import com.msa.myexpenses.databinding.ActivityValidationBinding
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.models.Users
import com.msa.myexpenses.models.Validation
import com.msa.myexpenses.room_db.database.DatabaseClient

class ValidationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityValidationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Validation.type == "forgetPassword") {
            binding.tvNote.visibility = TextView.INVISIBLE
            binding.tvAnswer.text = R.string.answerQuestion.toString()
        }

        // عرض dialog بتاريخ 1/1/2000 لاختيار تاريخ الميلاد عند الضغط على editText تاريخ الميلاد
        binding.etBirthdate.setOnClickListener {
            val picker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    binding.etBirthdate.setText("$d/${m + 1}/$y")
                }, 2000, 1, 1)
            picker!!.show()
        }

        binding.btnSaveAnswer.setOnClickListener {
            val birthdate = binding.etBirthdate.text.toString().trim()
            val animal = binding.etAnimal.text.toString().trim()
            val color = binding.etColor.text.toString().trim()
            val drink = binding.etDrink.text.toString().trim()

            if (birthdate.isEmpty() || animal.isEmpty() || color.isEmpty() || drink.isEmpty()) {
                Toast.makeText(this, R.string.fillFields, Toast.LENGTH_SHORT).show()
            } else {
                // اذا المستخدم فتح الواجهة من بعد انشاء حساب, بدي أخزن قيم المستخد بجدول المستخدمين وأخزن الأجوبة بجدول Validation
                if (Validation.type == "answerQuestions") {
                    val name = intent.getStringExtra("name").toString()
//                    val mobile = intent.getStringExtra("mobile").toString()
                    val email = intent.getStringExtra("email").toString()
                    val password = intent.getStringExtra("password").toString()

                    val validation = Validation(email, birthdate, animal, color, drink)
                    DatabaseClient.getInstance(this)!!.appDatabase.validationDao().insertValidation(validation)

                    // بدون ID لانو بزيد تلقائي
                    val user = Users()
                    user.name = name
//                    user.mobile = mobile
                    user.email = email
                    user.password = password
                    DatabaseClient.getInstance(this)!!.appDatabase.userDao().insertUser(user)

                    // اخراج dialog يسأل عن حفظ بياناته او لا
                    rememberDialog(email)

                    // اذا فتح الواجهة بسبب نسيان كلمة المرور
                } else if (Validation.type == "forgetPassword") {
                    val email = intent.getStringExtra("email").toString()
                    val answers = DatabaseClient.getInstance(this)!!.appDatabase.validationDao().getAnswers(email)
                    if (answers.isEmpty()) {
                        Toast.makeText(this, R.string.incorrectlyEmail, Toast.LENGTH_SHORT).show()
                    } else {
                        // فقط صف واحد بيرجع من الquery .. عشان هيك ببحث بقيم أول index
                        if (answers[0].birthdate == birthdate && answers[0].animal == animal &&
                            answers[0].color == color && answers[0].drink == drink) {
                            Toast.makeText(this, R.string.provenOwner, Toast.LENGTH_SHORT).show()

                            rememberDialog(email)
                        }else{
                            Toast.makeText(this, R.string.wrongAnswer, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun rememberDialog(email:String){
        val sharedEmail = getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
        val myEmail = sharedEmail.edit()
        myEmail.putString("email", email)
        myEmail.apply()

        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.remember_me)
        alert.setMessage(R.string.saveRegistration)
        alert.setCancelable(false)

        alert.setPositiveButton("Yes") { d, i ->
            val rememberMe = getSharedPreferences("Remember", MODE_PRIVATE)
            val editor = rememberMe.edit()
            editor.putBoolean("remember", true)
            editor.apply()

            startActivity(Intent(this@ValidationActivity, HomeActivity::class.java))
            finish()
        }

        alert.setNegativeButton("No") { d, i ->
            startActivity(Intent(this@ValidationActivity, HomeActivity::class.java))
            finish()
        }
        alert.create().show()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ValidationActivity, SignUpActivity::class.java))
        finish()
    }
}