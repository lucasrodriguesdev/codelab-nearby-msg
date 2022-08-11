package br.com.nearbymessages

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import br.com.nearbymessages.databinding.ActivityMainBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    /**
     * For accessing layout variables
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Sets the time to live in seconds for the publish or subscribe.
     */
    private val TTL_IN_SECONDS = 120 // Two minutes.

    /**
     * Choose of strategies for publishing or subscribing for nearby messages.
     */
    private val PUB_SUB_STRATEGY = Strategy.Builder().setTtlSeconds(TTL_IN_SECONDS).build()

    /**
     * The [Message] object used to broadcast information about the device to nearby devices.
     */
    private lateinit var message: Message

    /**
     * A [MessageListener] for processing messages from nearby devices.
     */
    private lateinit var messageListener: MessageListener

    /**
     * MessageAdapter is a custom class that we will define later. It's for adding
     * [messages][Message] to the [RecyclerView]
     */
    private lateinit var msgAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupMessagesDisplay()
        binding.subscribeSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked){
                subscribe()
            } else {
                unsubscribe()
            }
        }

        binding.publishSwitch.setOnCheckedChangeListener { button, isCheked ->
            if (isCheked){
                publish()
            } else {
                unpublish()
            }
        }
        message = Message(Build.MODEL.toByteArray(Charset.forName("UTF-8")))


        messageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                // Called when a new message is found.
                val msgBody = String(message.content)
                msgAdapter.addItem(msgBody)
            }

            override fun onLost(message: Message) {
                // Called when a message is no longer detectable nearby.
                val msgBody = String(message.content)
                msgAdapter.removeItem(msgBody)
            }
        }

        setContentView(binding.root)
    }

    private fun publish() {
        val options = PublishOptions.Builder()
            .setStrategy(PUB_SUB_STRATEGY)
            .setCallback(object : PublishCallback() {
                override fun onExpired() {
                    super.onExpired()
                    // flick the switch off since the publishing has expired.
                    // recall that we had set expiration time to 120 seconds
                    // Use runOnUiThread to force the callback
                    // to run on the UI thread
                    runOnUiThread{
                        binding.publishSwitch.isChecked = false
                    }
                }
            }).build()

        Nearby.getMessagesClient(this).publish(message, options)
    }

    private fun unpublish() {
        Nearby.getMessagesClient(this).unpublish(message)
    }

    private fun subscribe() {
        Nearby.getMessagesClient(this).unsubscribe(messageListener)
    }

    private fun unsubscribe() {
        val options = SubscribeOptions.Builder()
            .setStrategy(PUB_SUB_STRATEGY)
            .setCallback(object : SubscribeCallback() {
                override fun onExpired() {
                    super.onExpired()
                    // flick the switch off since the subscribing has expired.
                    // recall that we had set expiration time to 120 seconds
                    // Use runOnUiThread to force the callback
                    // to run on the UI thread
                    runOnUiThread {
                        binding.subscribeSwitch.isChecked = false
                    }
                }
            }).build()

        Nearby.getMessagesClient(this).subscribe(messageListener, options)
    }

    private fun setupMessagesDisplay() {
        msgAdapter = MessageAdapter()
        with(binding.nearbyMsgRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            this.adapter = msgAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // although the API should shutdown its processes when the client process dies,
        // you may want to stop subscribing (and publishing if convenient)
        Nearby.getMessagesClient(this).unpublish(message)
        Nearby.getMessagesClient(this).unsubscribe(messageListener)
    }
}