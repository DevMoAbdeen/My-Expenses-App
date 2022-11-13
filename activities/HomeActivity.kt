package com.msa.myexpenses.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.msa.myexpenses.R
import com.msa.myexpenses.databinding.ActivityHomeBinding
import com.msa.myexpenses.fragments.HomeFragment
import com.msa.myexpenses.fragments.ProfileFragment
import com.msa.myexpenses.fragments.SettingFragment
import com.msa.myexpenses.room_db.database.DatabaseClient

class HomeActivity : LocalizationActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedEmail = getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
        val email = sharedEmail.getString("email", "").toString()
        if(email.equals("test-acount@gmail.com")){
            binding.bottomNav.menu.getItem(1).isEnabled = false
            binding.bottomNav.menu.getItem(1).isVisible = false
        }

        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.nav_profile -> {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_write_password)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.setCancelable(false)
                    dialog.findViewById<ImageButton>(R.id.img_close).setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.findViewById<Button>(R.id.btn_update).setOnClickListener {
                        val passwordWritten = dialog.findViewById<EditText>(R.id.et_passwordToUpdate).text.toString().trim()
                        if(passwordWritten.isEmpty()){
                            dialog.findViewById<EditText>(R.id.et_passwordToUpdate).setError("${R.string.writePassword}")
                        }else{
                            //بجيب الايميل من sharedPref ووبحث عنه بالداتا بيز والobject اللي برجع باخذ منه الكلمة عشان أقارنها بالمكتوبة
                            val sharedEmail = getSharedPreferences("MyEmail", MODE_PRIVATE)
                            val email = sharedEmail.getString("email", "").toString()
                            val correctPassword = DatabaseClient.getInstance(this)!!.appDatabase.userDao().getPassword(email)

                            if(passwordWritten.equals(correctPassword)){
                                dialog.dismiss()
                                replaceFragment(ProfileFragment())
                            }else{
                                Toast.makeText(this, R.string.wrongPass, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.show()

                    return@setOnItemSelectedListener false
                }

                else -> {
                    replaceFragment(SettingFragment())
                    return@setOnItemSelectedListener true
                }
            }
        }

    }

    fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        fm.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onBackPressed() {
        finish()
    }

}