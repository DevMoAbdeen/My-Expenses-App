package com.msa.myexpenses.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.akexorcist.localizationactivity.core.LanguageSetting
import com.msa.myexpenses.R
import com.msa.myexpenses.activities.LoginActivity
import com.msa.myexpenses.databinding.FragmentSettingBinding
import com.msa.myexpenses.room_db.database.DatabaseClient
import java.util.*

class SettingFragment : Fragment() {
    private lateinit var settingFragment: FragmentSettingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        settingFragment = FragmentSettingBinding.inflate(inflater, container, false)

        // هنا بس بغير على قيم الاعدادات حسب شو اختار المستخدم
        val sharedSetting = requireActivity().getSharedPreferences("SettingsApp", AppCompatActivity.MODE_PRIVATE)
        val setting = sharedSetting.edit()

        if(sharedSetting.getString("language", "arabic") == "english"){
            settingFragment.rbEnglish.isChecked = true
        }else{
            settingFragment.rbArabic.isChecked = true
        }

        if(sharedSetting.getString("theme", "light") == "dark"){
            settingFragment.rbDark.isChecked = true
        }else{
            settingFragment.rbLight.isChecked = true
        }

        if(sharedSetting.getBoolean("notification", false)){
            settingFragment.rbOn.isChecked = true
        }else{
            settingFragment.rbOff.isChecked = true
        }

        ////////////////////////////////////////////////////////

        // بحفظ كل تغيير يعمله المستخدم على sharedPref وبغير اعدادات اللغة والTheme حسب شو اختار المستخدم
        // اكواد التغيير جاهزة من النت
        settingFragment.rbArabic.setOnClickListener {
            LanguageSetting.setLanguage(requireActivity(), Locale("ar"))
            setting.putString("language", "arabic").apply()
            requireActivity().recreate()
        }

        settingFragment.rbEnglish.setOnClickListener{
            LanguageSetting.setLanguage(requireActivity(), Locale("en"))
            setting.putString("language", "english").apply()
            requireActivity().recreate()
        }

        settingFragment.rbLight.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            requireActivity().recreate()
            setting.putString("theme", "light").apply()
        }

        settingFragment.rbDark.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            requireActivity().recreate()
            setting.putString("theme", "dark").apply()
        }

        settingFragment.rbOn.setOnClickListener{
            setting.putBoolean("notification", true).apply()
        }

        settingFragment.rbOff.setOnClickListener{
            setting.putBoolean("notification", false).apply()
        }

        settingFragment.tvLogout.setOnClickListener {
            val alert = AlertDialog.Builder(requireActivity())
            alert.setTitle(R.string.Logout)
            alert.setMessage(R.string.sureLogout)
            alert.setIcon(R.drawable.ic_logout)
            alert.setCancelable(true)

            alert.setPositiveButton(R.string.yes){d, i ->
                val rememberMe = requireActivity().getSharedPreferences("Remember", MODE_PRIVATE)
                val remember = rememberMe.edit()
                remember.remove("remember").apply()

                val i = Intent(activity, LoginActivity::class.java)
                startActivity(i)
                requireActivity()!!.finish()
            }

            alert.setNegativeButton(R.string.cancel){d, i ->
                d.cancel()
            }
            alert.create().show()
        }

        // حذف الحساب مع كل البيانات المتعلقة به بشكل نهائي
        settingFragment.tvQDelete.setOnClickListener {
            settingFragment.tvDeleteAccount.visibility = TextView.VISIBLE
        }

        settingFragment.tvDeleteAccount.setOnClickListener {
            val alert = AlertDialog.Builder(requireActivity())
            alert.setTitle(R.string.delete_account)
            alert.setIcon(R.drawable.ic_delete)
            alert.setMessage(R.string.note_delete)
            alert.setCancelable(true)

            alert.setPositiveButton(R.string.confirmDelete){d, i ->
                val rememberMe = requireActivity().getSharedPreferences("Remember", MODE_PRIVATE)
                val remember = rememberMe.edit()
                remember.remove("remember").apply()

                val sharedEmail = requireActivity().getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
                val email = sharedEmail.getString("email", "").toString()

                DatabaseClient.getInstance(requireActivity())!!.appDatabase.userDao().deleteUser(email)
                DatabaseClient.getInstance(requireActivity())!!.appDatabase.expenseDao().deleteExpensesUser(email)
                DatabaseClient.getInstance(requireActivity())!!.appDatabase.validationDao().deleteValidationUser(email)

                val intent = Intent(requireActivity(), LoginActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }

            alert.setNegativeButton(R.string.cancel){d, i ->
                d.cancel()
            }
            alert.create().show()
        }

        return settingFragment.root
    }

}