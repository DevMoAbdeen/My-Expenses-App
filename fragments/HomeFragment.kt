package com.msa.myexpenses.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.msa.myexpenses.R
import com.msa.myexpenses.adapters.MyExpensesAdapter
import com.msa.myexpenses.broadcast_receiver.*
import com.msa.myexpenses.databinding.FragmentHomeBinding
import com.msa.myexpenses.models.Expenses
import com.msa.myexpenses.room_db.database.DatabaseClient
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var homeBinding: FragmentHomeBinding
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "msaTAG"
    var data = mutableListOf<Expenses>()
    lateinit var email : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        // عشان أضيف الاعلان على الشاشة كاملة
        MobileAds.initialize(requireContext())
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), "ca-app-pub-7581259865493448/2983750128", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })


        // بجيب الايميل اللي حفظته اول ما سجل المستخدم دخول
        val sharedEmail = requireActivity().getSharedPreferences("MyEmail", AppCompatActivity.MODE_PRIVATE)
        email = sharedEmail.getString("email", "").toString()

        // بجيب كل الexpenses الخاصة بالمستخدم
        data = DatabaseClient.getInstance(activity)!!.appDatabase.expenseDao().getAllExpenses(email)
        homeBinding.recyclerExpenses.layoutManager = LinearLayoutManager(activity)
        val expensesAdapter = MyExpensesAdapter(requireActivity(), data, object : MyExpensesAdapter.OnExpensesListener{
            override fun deleteExpenses(position: Int) {
                // اذا كانت الarray فيها قيم بحذف العنصر اللي وصلني الposition الخاص فيه من الadapter
                if(data.isNotEmpty()) {
                    data.removeAt(position)
                }
                // بفحص اذا في بيانات او لا.. لو فش بيانات يعرض الصورة وجملة No Expenses
                checkData()
            }
        })
        homeBinding.recyclerExpenses.adapter = expensesAdapter
        // بفحص اذا في بيانات او لا.. لو فش بيانات يعرض الصورة وجملة No Expenses
        checkData()

        homeBinding.showDialog.setOnClickListener {
            val bottomDialog = BottomDialogFragment(object : BottomDialogFragment.onExpensesListener {
                override fun addExpense(expense: Expenses) {
                    // بضيف الobject اللي وصلني من المستخدم منBottomDialogFragment على الarray اللي فيها البيانات
                    data.add(expense)
                    // بفحص اذا في بيانات او لا.. لو فش بيانات يعرض الصورة وجملة No Expenses
                    checkData()
                    expensesAdapter.notifyDataSetChanged()
                }

                override fun editExpenses(expense: Expenses) {}

            })
            Expenses.type = "newExpense"
            bottomDialog.show(requireActivity().supportFragmentManager, "newExpense")
        }

        return homeBinding.root

    }

    private fun checkData(){
        if (data.isEmpty()) {
            homeBinding.linearInHome.visibility = LinearLayout.VISIBLE
        }else{
            homeBinding.linearInHome.visibility = LinearLayout.GONE
        }

    }

    // الكود الخاص بعملية البحث.. برجع المشتريات اللي تمت بتاريخ محدد يختاره المستخد
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var searchExpense = mutableListOf<Expenses>()
        when(item.itemId){
            R.id.search -> {
                Toast.makeText(activity, R.string.selectDate, Toast.LENGTH_SHORT).show()
                val currentDate = Calendar.getInstance()
                val day = currentDate.get(Calendar.DAY_OF_MONTH)
                val month = currentDate.get(Calendar.MONTH)
                val year = currentDate.get(Calendar.YEAR)

                val picker = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    val day = if(d < 10) "0$d" else "$d"
                    val month = if(m + 1 < 10) "0${m + 1}" else "${m + 1}"
                    val date = "$y/$month/$day"
                    searchExpense = DatabaseClient.getInstance(requireActivity())!!.appDatabase.expenseDao().searchExpenses(email, date)

                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        Log.d(TAG, "The interstitial ad wasn't ready yet.")
                    }

                    if (searchExpense.isEmpty()) {
                        homeBinding.linearInHome.visibility = LinearLayout.VISIBLE
                        homeBinding.tvNoExpenses.text = "No expenses in $date"
                    }else{
                        homeBinding.linearInHome.visibility = LinearLayout.GONE
                    }

                    homeBinding.recyclerExpenses.layoutManager = LinearLayoutManager(activity)
                    val expensesAdapter = MyExpensesAdapter(requireActivity(), searchExpense, object: MyExpensesAdapter.OnExpensesListener{
                        override fun deleteExpenses(position: Int) {}

                    })
                    homeBinding.recyclerExpenses.adapter = expensesAdapter

                }, year, month, day)
                picker!!.show()

            }
        }

        return super.onOptionsItemSelected(item)
    }

}