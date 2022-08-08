package com.firstwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.ExecutionException


class HomeActivity : AppCompatActivity() {
    protected var myHandler: Handler? = null
    var receivedMessageNumber = 1
    var sentMessageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        myHandler = Handler { msg ->
            val stuff = msg.data
            messageText(stuff.getString("messageText")!!)
            true
        }
        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = Receiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)

    }

    private fun messageText(newinfo: String) {
        if (newinfo.compareTo("") != 0) {
            txt_add.append(
                """
                
                $newinfo
                """.trimIndent()
            )
        }
    }

    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Received a message from the watch", Toast.LENGTH_LONG).show()
        }
    }

    fun talkClick(v: View?) {
        val message = "Sending message.... "
        txt_add.text = message

        NewThread("/my_path", message, this).start()
    }

    fun sendmessage(messageText: String?) {
        val bundle = Bundle()
        bundle.putString("messageText", messageText)
        val msg: Message = myHandler!!.obtainMessage()
        msg.data = bundle
        myHandler!!.sendMessage(msg)
    }

    inner class NewThread
        (var path: String, var message: String, var context: Context?) : Thread() {
        override fun run() {

            val wearableList: Task<List<Node>> =
                Wearable.getNodeClient(context).connectedNodes
            try {
                val nodes: List<Node> = Tasks.await<List<Node>>(wearableList)
                for (node in nodes) {
                    val sendMessageTask: Task<Int> =
                        Wearable.getMessageClient(context)
                            .sendMessage(node.id, path, message.toByteArray())
                    try {
                        val result = Tasks.await<Int>(sendMessageTask)
                        sendmessage("Sent the message to watch")


                    } catch (exception: ExecutionException) {

                    } catch (exception: InterruptedException) {

                    }
                }
            } catch (exception: ExecutionException) {
            } catch (exception: InterruptedException) {

            }
        }
    }
}