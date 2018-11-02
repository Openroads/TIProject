package pl.documenteditor.documenteditor.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import pl.documenteditor.documenteditor.R
import pl.documenteditor.documenteditor.model.Message


class MessageAdapter(private val context: Context) : BaseAdapter() {

    private var messages: MutableList<Message> = ArrayList()

    fun add(message: Message) {
        this.messages.add(message)
        notifyDataSetChanged() // to render the list we need to notify
    }

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(i: Int): Any {
        return messages[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    override fun getView(i: Int,  convertView: View?, viewGroup: ViewGroup): View {
        val holder = MessageViewHolder()
        val messageInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val message = messages[i]

        val view: View
        if (message.belongsToCurrentUser) { // this message was sent by us so let's create a basic chat bubble on the right
            view = messageInflater.inflate(R.layout.my_message, null)
            holder.messageBody = view.findViewById(R.id.message_body)
            view.tag = holder
            holder.messageBody!!.text = message.text
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            view = messageInflater.inflate(R.layout.their_message, null)
            holder.name = view.findViewById(R.id.name)
            holder.messageBody = view.findViewById(R.id.message_body)
            convertView?.tag = holder

            holder.name!!.text = message.username
            holder.messageBody!!.text = message.text
        }

        return view
    }

}

internal class MessageViewHolder {
    var avatar: View? = null
    var name: TextView? = null
    var messageBody: TextView? = null
}