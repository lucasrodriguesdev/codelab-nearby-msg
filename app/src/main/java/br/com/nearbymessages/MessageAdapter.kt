package br.com.nearbymessages

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter: RecyclerView.Adapter<MessageAdapter.MessageVH>() {

    private var itemsList: MutableList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        return MessageVH(TextView(parent.context))
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = itemsList.size

    private fun getItem(pos: Int): String? = if (itemsList.isEmpty()) null else itemsList[pos]

    fun addItem(item: String) {
        itemsList.add(item)
        notifyItemInserted(itemsList.size)
    }

    fun removeItem(item: String) {
        val pos = itemsList.indexOf(item)
        itemsList.remove(item)
        notifyItemRemoved(pos)
    }

    inner class MessageVH(private val tv: TextView) : RecyclerView.ViewHolder(tv) {
        fun bind(item: String?) {
            item?.let { tv.text = it }
        }
    }
}
