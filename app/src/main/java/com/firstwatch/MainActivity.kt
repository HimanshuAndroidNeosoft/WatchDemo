package com.firstwatch

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.firstwatch.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.ExecutionException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    var receivedMessageNumber = 1
    var sentMessageNumber = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btn_snd.setOnClickListener {
            val onClickMessage = "Sent message to app " + sentMessageNumber++
            text.text = onClickMessage

            val datapath = "/my_path"
            SendMessage(datapath, onClickMessage, this).start()
        }

        val newFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = Receiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter)

    }

    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Received a message from the watch", Toast.LENGTH_LONG).show()
        }
    }

    internal class SendMessage     //Constructor for sending information to the Data Layer//
        (var path: String, var message: String, var context: Context?) : Thread() {
        override fun run() {

            val nodeListTask: Task<List<Node>> =
                Wearable.getNodeClient(context).connectedNodes
            try {
                val nodes: List<Node> = Tasks.await<List<Node>>(nodeListTask)
                for (node in nodes) {

                    val sendMessageTask: Task<Int> = Wearable.getMessageClient(context)
                        .sendMessage(node.id, path, message.toByteArray())
                    try {
                        val result = Tasks.await<Int>(sendMessageTask)

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