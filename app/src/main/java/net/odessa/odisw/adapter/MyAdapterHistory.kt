package net.odessa.odisw.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.history_for_send.view.*
import kotlinx.android.synthetic.main.wait_for_send.view.*
import kotlinx.android.synthetic.main.wait_for_send.view.msg_text
import net.odessa.odisw.ApplicationLevel
import net.odessa.odisw.R
import net.odessa.odisw.http.AsyncHttp
import net.odessa.odisw.model.Request

class MyAdapterHistyory(val items : MutableList<Request>, val context: Context) : RecyclerView.Adapter<ViewHolderInner>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderInner {
        return ViewHolderInner(LayoutInflater.from(context).inflate(R.layout.history_for_send, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderInner, position: Int) {
        holder.text.text = (position+1).toString()+". "+items[position].time +" "+ items[position].sn +" "+ items[position].coord[0] +" "+ items[position].coord[1]
        if (items[position].isSended){holder.iv_color.setBackgroundResource(R.color.green)} else {holder.iv_color.setBackgroundResource(R.color.red)}
        holder.share_history.setOnClickListener {
            val gson = Gson()
            val json = gson.toJson(items[position])
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, json)

            context.startActivity(Intent.createChooser(intent, "Сообщение"))
        }
    }
}

class ViewHolderInner (view: View) : RecyclerView.ViewHolder(view) {
    val text = view.msg_text
    val iv_color = view.status_iv
    val share_history = view.share_history
}