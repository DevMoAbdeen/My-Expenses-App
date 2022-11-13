package com.msa.myexpenses.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.msa.myexpenses.R
import com.msa.myexpenses.broadcast_receiver.BootReceiver
import com.msa.myexpenses.databinding.ActivitySplashBinding
import com.msa.myexpenses.room_db.database.DatabaseClient

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ////////////////////////////
//        DatabaseClient.getInstance(this)!!.appDatabase.expenseDao().deleteAllExpenses()
//        DatabaseClient.getInstance(this)!!.appDatabase.userDao().deleteAllUsers()
//        DatabaseClient.getInstance(this)!!.appDatabase.validationDao().deleteAllValidation()
//
//        val userID = getSharedPreferences("MyID", MODE_PRIVATE)
//        val expenseID = getSharedPreferences("expenseID", MODE_PRIVATE)
//        val rememberMe2 = getSharedPreferences("Remember", MODE_PRIVATE)
//
//        val user = userID.edit()
//        val expense = expenseID.edit()
//        val remember2 = rememberMe2.edit()
//
//        user.remove("MyID").apply()
//        expense.remove("expenseID").apply()
//        remember2.remove("remember").apply()
            ////////////////////////////
        val sharedSetting = getSharedPreferences("SettingsApp", MODE_PRIVATE)
        val notification = sharedSetting.getBoolean("notification", false)
        if(notification){
            val bootReceiver = BootReceiver()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val intentFilter = IntentFilter()
                intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED)
                intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                registerReceiver(bootReceiver, intentFilter)
            }
        }

        val rememberMe = getSharedPreferences("Remember", MODE_PRIVATE)
        val remember = rememberMe.getBoolean("remember", false)

        // عمل animation .. عرض شكل عند فتح الواجهة
        val animationRotate = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        binding.linearInSplash.startAnimation(animationRotate)
        animationRotate.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                // اذا في بيانات محفوظة يفتح الhome مباشرة
                if(remember){
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

    }
}