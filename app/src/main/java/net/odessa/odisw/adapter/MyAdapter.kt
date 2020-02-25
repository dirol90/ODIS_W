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
import kotlinx.android.synthetic.main.wait_for_send.view.*
import net.odessa.odisw.ApplicationLevel
import net.odessa.odisw.R
import net.odessa.odisw.http.AsyncHttp
import net.odessa.odisw.model.Request

class MyAdapter(val items : MutableList<Request>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.wait_for_send, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].time +" "+ items[position].sn +" "+ items[position].coord[0] +" "+ items[position].coord[1]

        holder.brn2.setOnClickListener {

            val gson = Gson()
            val json = gson.toJson(items[position])
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, json)

            context.startActivity(Intent.createChooser(intent, "Сообщение"))
        }

        holder.btn1.setOnClickListener {
            val isOk = AsyncHttp(
                ApplicationLevel.loadServerUri(),
                "380${ApplicationLevel.loadPhone()}",
                items[position]
            ).execute()

            if (isOk) {
                Toast.makeText( context, "ОТПРАВЛЕНО УСПЕШНО!", Toast.LENGTH_LONG).show()
                items[position].isSended = true
                ApplicationLevel.setDataFromSharedPreferences(ApplicationLevel.requests)
                items.remove(items[position])
                notifyDataSetChanged()
            } else {
                Toast.makeText(
                    context,
                    "ОШИБКА ПРИ ОТПРАВКЕ СООБЩЕНИЯ! В ОЧЕРЕДИ!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val text = view.msg_text
    val btn1 = view.send_via_msgr
    val brn2 = view.send_one_more_time
}