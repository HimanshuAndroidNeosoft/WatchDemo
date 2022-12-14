package com.firstwatch

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService


class MessageService: WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {

        if (messageEvent.path.equals("/my_path")) {
            val message = String(messageEvent.data)
            val messageIntent = Intent()
            messageIntent.action = Intent.ACTION_SEND
            messageIntent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
        } else {
            super.onMessageReceived(messageEvent)
        }
    }
}