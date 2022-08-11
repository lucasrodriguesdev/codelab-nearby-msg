package br.com.nearbymessages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import br.com.nearbymessages.databinding.ActivityMainBinding
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Strategy

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
        setContentView(binding.root)
    }
}