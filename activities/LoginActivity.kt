package com.msa.myexpenses.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginTop
import com.msa.myexpenses.R
import com.msa.myexpenses.databinding.ActivityLoginBinding
import com.msa.myexpenses.databinding.ActivitySplashBinding
import com.msa.myexpenses.models.Users
import com.msa.myexpenses.models.Validation
import com.msa.myexpenses.room_db.database.DatabaseClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.fillFields, Toast.LENGTH_SHORT).show()
            } else {
                // بحدد بيانات لو انكتبت يدخل على التطبيق عشان يتم تجريب التطبيق قبل ما ينزل على المتجر
                if (email.equals("test-acount@gmail.com") &&
                    password.equals("test123")) {
                    val sharedEmail = getSharedPreferences("MyEmail", MODE_PRIVATE)
                    val email = sharedEmail.edit()
                    email.putString("email", binding.etEmail.text.toString().trim())
                    email.apply()

                    if (binding.cbRememberMe.isChecked) {
                        val rememberMe = getSharedPreferences("Remember", MODE_PRIVATE)
                        val editor = rememberMe.edit()
                        editor.putBoolean("remember", true)
                        editor.apply()
                    }

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    val isTrue = DatabaseClient.getInstance(this)!!.appDatabase.userDao().login(email, password)
                    // اذا isTrue كانت فاضية يعني كتب الايميل او الكلمة غلط
                    if (isTrue.isEmpty()) {
                        // ببحث عن الايميل واذا كان موجود يبقى كتبه صح وكتب الكلمة غلط.. اذا مش موجود يعني كتب الايميل غلط
                        val isExist = DatabaseClient.getInstance(this)!!.appDatabase.userDao().isExist(email)
                        // اذا كان الايميل صح فقط هيرجع صف واحد.. عشان هيك ببحث بقيم الindex الاول 0
                        if (isExist.isNotEmpty() && isExist[0].email == email) {
                            binding.tvForget.visibility = TextView.VISIBLE
                            Toast.makeText(this, R.string.wrongPass, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, R.string.notRegister, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // هنا يعني كتب الايميل والكلمة بشكل صحيح.. وبشوف اذا الCheckBox قيمته true بحفظ البيانات
                        val sharedEmail = getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
                        val email = sharedEmail.edit()
                        email.putString("email", binding.etEmail.text.toString().trim())
                        email.apply()

                        if (binding.cbRememberMe.isChecked) {
                            val rememberMe = getSharedPreferences("Remember", MODE_PRIVATE)
                            val editor = rememberMe.edit()
                            editor.putBoolean("remember", true)
                            editor.apply()
                        }

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }

        binding.tvForget.setOnClickListener {
            Validation.type = "forgetPassword"
            val intent = Intent(this, ValidationActivity::class.java)
            intent.putExtra("email", binding.etEmail.text.toString().trim())
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        finish()
    }
}