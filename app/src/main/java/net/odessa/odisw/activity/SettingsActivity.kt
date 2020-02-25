package net.odessa.odisw.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_settings.*
import net.odessa.odisw.ApplicationLevel
import net.odessa.odisw.R
import net.odessa.odisw.adapter.MyAdapterHistyory
import net.odessa.odisw.http.AsyncHttp
import java.io.File
import java.io.FileWriter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        user_phone_et.text = "380${ApplicationLevel.loadPhone()}"
        server_uri_et.text = ApplicationLevel.loadServerUri().toEditable()
        preambula_et.text = ApplicationLevel.loadPreambula().toEditable()

        close_btn.setOnClickListener {
            onBackPressed()
        }

        save_btn.setOnClickListener {

            var isOkPath: Boolean = if (!server_uri_et.text.startsWith("http://") && !server_uri_et.text.startsWith("https://")){
                Toast.makeText(this, "Не верный путь к серверу, проверьте повторно или обратитесь к разработчику ПО",  Toast.LENGTH_LONG).show()
                false
            } else {
                ApplicationLevel.saveServerUri(server_uri_et.text.toString())
                true
            }

            var isOkPreambula: Boolean = if (preambula_et.text.isEmpty()){
                Toast.makeText(this, "Преамбула пуста, внесите стартовое значение!",  Toast.LENGTH_LONG).show()
                false
            } else {
                ApplicationLevel.savePreambula(preambula_et.text.toString())
                true
            }

            if (isOkPath && isOkPreambula) {onBackPressed()}
        }

        send_history_btn.setOnClickListener {

            shadow_ll_history.visibility = View.VISIBLE

            for(i in 0 until ApplicationLevel.requests.size){
                val isOk = AsyncHttp(
                    ApplicationLevel.loadServerUri(),
                    "380${ApplicationLevel.loadPhone()}",
                    ApplicationLevel.requests[i]
                ).execute()

                if (isOk) {
                    ApplicationLevel.requests[i].isSended = true
                }
            }

            ApplicationLevel.setDataFromSharedPreferences(ApplicationLevel.requests)
            updateRucycler()

            shadow_ll_history.visibility = View.GONE
            Toast.makeText(this, "История отправлена",  Toast.LENGTH_LONG).show()
        }

        share_history_btn.setOnClickListener {
            var body = ""
            for(i in 0 until ApplicationLevel.requests.size){

                val gson = GsonBuilder()
                    .setExclusionStrategies()
                    .create()
                var json = gson.toJson(ApplicationLevel.requests[i])

                try {
                    json = json.replace("\\u003d", "=")
                    json = json.replace(",\"isSended\":false", "")
                    json = json.replace(",\"isSended\":true", "")
                } catch (e: Exception) {
                }

                body += json + "\n"
            }

            sendFile(this, body)

        }

        updateRucycler()

    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun updateRucycler(){
        ApplicationLevel.requests = ArrayList()
        ApplicationLevel.requests = ApplicationLevel.getDataFromSharedPreferences()
        if (ApplicationLevel.requests != null) {
            val adapter = MyAdapterHistyory(ApplicationLevel.requests!!.asReversed(), this)
            val linearLayoutManager = LinearLayoutManager(this)
            rw_history.layoutManager = linearLayoutManager
            rw_history.adapter = adapter
        }
    }


    private fun sendFile(context: Context, body: String) {
        val format = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        val date = Date(System.currentTimeMillis())
        var convertedDate = ""
        try {
            convertedDate = format.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val dirpath: String =
            context.filesDir.toString() + File.separator + "directory"
        val file = File(dirpath + File.separator + "ODIS_W", "ODIS_W_log_$convertedDate.txt")

        if (!file.exists()) {
            file.parentFile.mkdirs()
        }

        val uri: Uri = FileProvider.getUriForFile(context, "net.odessa.odisw.fileprovider", file)

        val writer = FileWriter(file, true)
        writer.append(body)
        writer.flush()
        writer.close()

        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val resInfoList: List<ResolveInfo> = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent)
        }
    }

}
