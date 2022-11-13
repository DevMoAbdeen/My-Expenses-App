package com.msa.myexpenses.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.msa.myexpenses.R
import com.msa.myexpenses.databinding.ActivityLoginBinding
import com.msa.myexpenses.databinding.ActivitySignUpBinding
import com.msa.myexpenses.databinding.ActivitySplashBinding
import com.msa.myexpenses.models.Users
import com.msa.myexpenses.room_db.database.DatabaseClient

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
//            val mobile = binding.etMobile.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.fillFields, Toast.LENGTH_SHORT).show()
            } else {
                // ببحث اذا الايميل او الرقم مسجلين من قبل او لا
                val isExist = DatabaseClient.getInstance(this)!!.appDatabase.userDao().isExist(email)
                if(isExist.isEmpty()){
                    // اذا isExist كانت فاضية يعني الايمل والرقم لاول مرة يسجلوا
                    val intent = Intent(this, ValidationActivity::class.java)
                    intent.putExtra("name", name)
//                    intent.putExtra("mobile", mobile)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, R.string.alreadyRegisterd, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}