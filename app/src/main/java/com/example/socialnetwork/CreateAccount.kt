package com.example.socialnetwork

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.chaos.view.PinView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hbb20.CountryCodePicker

class CreateAccount : AppCompatActivity() {
    lateinit var createAccount: Button
    lateinit var countryCode: CountryCodePicker
    lateinit var phoneNumber: EditText
    lateinit var txtCreateAccount: TextView
    lateinit var auth: FirebaseAuth
    lateinit var RootRef: DatabaseReference
    lateinit var callBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var sharedPreferences: SharedPreferences? = null
    var storedVerificationId: String? = null
    lateinit var progressBar: ProgressBar
    lateinit var verificationCodeLayout: LinearLayout
    lateinit var layout1: LinearLayout
    lateinit var verificationCodeInput: PinView
    lateinit var btn_ok: Button
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createaccount)
        layout1 = findViewById(R.id.layout1)
        progressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
        RootRef = FirebaseDatabase.getInstance().getReference()
        createAccount = findViewById(R.id.btn_createAccount)
        countryCode = findViewById(R.id.ed_countryCode)
        phoneNumber = findViewById(R.id.ed_phoneNumber)
        txtCreateAccount = findViewById(R.id.txtCreateAccount)
        verificationCodeInput = findViewById(R.id.verificationCodeInput)
        btn_ok = findViewById(R.id.btn_ok)

        txtCreateAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountEmail::class.java)
            startActivity(intent)
        }

        verificationCodeLayout = findViewById(R.id.verificationCodeLayout)
        if (!checkPermission()) {
            requestPermision()
        } else {
            recieveMsg()
        }
        btn_ok.setOnClickListener {
            if (checkInternet()) {
                Toast.makeText(this, verificationCodeInput.text.toString(), Toast.LENGTH_SHORT)
                    .show()
                progressBar.visibility = View.VISIBLE
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(),
                    verificationCodeInput.text.toString()
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Snackbar.make(it, "Internet not available", Snackbar.LENGTH_SHORT).show()
            }
        }
        createAccount.setOnClickListener {
            if (checkInternet()) {
                if (TextUtils.isEmpty(phoneNumber.text)) {
                    Snackbar.make(it, "Enter phone number", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.splash_background)).show()
                } else {
                    progressBar.visibility = View.VISIBLE
                    countryCode.visibility = View.INVISIBLE
                    createAccount.visibility = View.INVISIBLE
                    phoneNumber.visibility = View.INVISIBLE
                    verificationCodeLayout.visibility = View.VISIBLE
                    val fullPhoneNumber: String =
                        "+" + countryCode.fullNumberWithPlus.toString() + phoneNumber.text.toString()

                    val option = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(fullPhoneNumber)
                        .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callBacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(option)
                }
            } else {
                val snackbar = Snackbar.make(it, "Internet not available", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }
        callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(baseContext, p0.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                Toast.makeText(baseContext, "Code Sent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePrefData() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("isCreated", true)
        editor.apply()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val currentUserId = auth.currentUser!!.uid
                    val profileMap: HashMap<String, String> = HashMap()
                    profileMap.put("profileImage", "")
                    profileMap.put("uid", currentUserId)
                    profileMap.put("name", "")
                    profileMap.put("phoneNumber", phoneNumber.text.toString())
                    RootRef.child("Users").child(currentUserId).setValue(profileMap)
                    progressBar.visibility = View.INVISIBLE
                    intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    savePrefData()
                    finish()
                } else {
                    Toast.makeText(this, "Verivication Code Wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun requestPermision() {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),1)
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_NUMBERS),1)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 1)
    }

    fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recieveMsg()
        }
    }

    private fun recieveMsg() {
        var br = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (sms: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
//                        Toast.makeText(
//                            applicationContext,
//                            sms.displayMessageBody.toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
                        verificationCodeInput.setText(sms.displayMessageBody.toString())
                    }
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    fun checkInternet(): Boolean {
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}