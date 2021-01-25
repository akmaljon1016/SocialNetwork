package com.example.socialnetwork

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast
import com.chaos.view.PinView

class OTP_Receiver() : BroadcastReceiver() {
    var editText: PinView? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)){
                editText?.setText(sms.displayMessageBody)
            }
        }
    }


    @JvmName("setEditText1")
    fun setEditText(editText1: PinView) {
        OTP_Receiver().editText = editText1
    }
}
