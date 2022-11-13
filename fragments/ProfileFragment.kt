package com.msa.myexpenses.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.msa.myexpenses.R
import com.msa.myexpenses.activities.HomeActivity
import com.msa.myexpenses.databinding.FragmentProfileBinding
import com.msa.myexpenses.models.Users
import com.msa.myexpenses.room_db.database.DatabaseClient

class ProfileFragment : Fragment() {
    private lateinit var profileFragment: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileFragment = FragmentProfileBinding.inflate(inflater, container, false)

        //بجيب الايميل من sharedPref ووبحث عنه بالداتا بيز والobject اللي برجع باخذ منه البيانات وبعرضها في الfields
        val sharedEmail = requireActivity().getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
        val email = sharedEmail.getString("email", "").toString()

        val oldData = DatabaseClient.getInstance(requireActivity())!!.appDatabase.userDao().isExist(email)
        try {
            val name = oldData[0].name
            val password = oldData[0].password

            profileFragment.etName.setText(name)
            profileFragment.etEmail.setText(email)
            profileFragment.etPassword.setText(password)
        } catch (ex: Exception) {
            Toast.makeText(requireActivity(), ex.message, Toast.LENGTH_LONG).show()
        }

        profileFragment.btnSave.setOnClickListener {
            val name = profileFragment.etName.text.toString().trim()
            val email = profileFragment.etEmail.text.toString().trim()
            val password = profileFragment.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireActivity(), R.string.fillFields, Toast.LENGTH_SHORT).show()
            } else {
                //  بجيب الid عشان ادخله مع البيانات اللي هعدلها لانو التعديل محتاج امرر له object كامل
                val id = oldData[0].Id
                val user = Users()
                user.Id = id
                user.name = name
                user.email = email
                user.password = password

                DatabaseClient.getInstance(requireActivity())!!.appDatabase.userDao().editUser(user)
                Toast.makeText(requireActivity(), R.string.update_data, Toast.LENGTH_SHORT).show()
                // برجع اعدل على الايميل المحفوظ في sharedPref
                val newEmail = sharedEmail.edit()
                newEmail.putString("email", email)
                newEmail.apply()

                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
            }

        }

        return profileFragment.root

    }

}
