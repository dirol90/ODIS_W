package net.odessa.odisw.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import net.odessa.odisw.ApplicationLevel
import net.odessa.odisw.ApplicationLevel.*
import net.odessa.odisw.R
import net.odessa.odisw.adapter.MyAdapter
import net.odessa.odisw.http.AsyncHttp
import net.odessa.odisw.model.Request
import net.odessa.odisw.provider.SingleShotLocationProvider
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity() {

    var isGPSOk = false
    var isSNOk = false
    var isStoreHouseLastClick = false
    var adapter: MyAdapter? = null
    var notSendedRequests: MutableList<Request>? = null

    var SECOND_ACTIVITY_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preambula_text.text = ApplicationLevel.loadPreambula()

        try {
            updateRucycler()

            gps_update.setOnClickListener {

                isStoreHouseLastClick = false

                isGPSOk = false
                gps_text.text = "ПОИСК СПУТНИКОВ..."
                gtp_image_status.setBackgroundResource(R.drawable.baseline_done_grey_18dp)
                sendBtnStatusListener()
                getGPS(this)
            }

            sn_edittext.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    if (s.length == 4) {
                        isSNOk = true
                        iv_sn.setBackgroundResource(R.drawable.baseline_done_black_18dp)
                        sendBtnStatusListener()
                        val view: View? = currentFocus
                        if (view != null) {
                            val imm: InputMethodManager =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                        }
                    } else {
                        isSNOk = false
                        iv_sn.setBackgroundResource(R.drawable.baseline_done_grey_18dp)
                        sendBtnStatusListener()
                    }
                }
            })

            menu_btn.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            button_map.setOnClickListener {
                if (lastLocation != null ) {
                    println("GO TO MAP!")
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("latitude",lastLocation!!.latitude.toString())
                    intent.putExtra("longitude",lastLocation!!.longitude.toString())
                    intent.putExtra("isStoreHouseLastClick", isStoreHouseLastClick)

                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
                } else {
                    Toast.makeText(this, "Не выбраны текущие координаты, нажмите кнопку ОБНОВИТЬ", Toast.LENGTH_LONG).show()
                }
            }

            button_house.setOnClickListener {

                val list = listOf(0, 1)
                val number = list.random()
                if (number == 0) {
                    lastLocation = SingleShotLocationProvider.GPSCoordinates(ApplicationLevel.loadXHouse().toFloat()+0.00005f, ApplicationLevel.loadYHouse().toFloat())
                } else {
                    lastLocation = SingleShotLocationProvider.GPSCoordinates(ApplicationLevel.loadXHouse().toFloat(), ApplicationLevel.loadYHouse().toFloat()+0.00005f)
                }

                ApplicationLevel.saveXHouse(lastLocation!!.latitude.toString())
                ApplicationLevel.saveYHouse(lastLocation!!.longitude.toString())

                gtp_image_status.setBackgroundResource(R.drawable.baseline_done_black_18dp)
                gps_text.text = lastLocation!!.latitude.toString() + "," + lastLocation!!.longitude.toString()

                isStoreHouseLastClick = true
                isGPSOk = true

                sendBtnStatusListener()
            }


        } catch (e : Exception){
            Toast.makeText(this, "Пожалуйста, удалите приложение и установите его заново", Toast.LENGTH_LONG).show()
            e.printStackTrace()}

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

           if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                lastLocation!!.latitude = data!!.getStringExtra("latitude")!!.toFloat()
                lastLocation!!.longitude = data.getStringExtra("longitude")!!.toFloat()
                gps_text.text = lastLocation!!.latitude.toString() + "," + lastLocation!!.longitude.toString()
            }
        }
    }

    var lastLocation: SingleShotLocationProvider.GPSCoordinates? = null

    fun getGPS(context: Context?) {
        SingleShotLocationProvider.requestSingleUpdate(
            context
        ) { location ->
            Log.d(
                "Location",
                "my location is " + location.latitude.toString() + " " + location.longitude.toString()
            )
            lastLocation = location
            gps_text.text = location.latitude.toString() + "," + location.longitude.toString()
            gtp_image_status.setBackgroundResource(R.drawable.baseline_done_black_18dp)
            isGPSOk = true
            sendBtnStatusListener()
        }
    }

    fun sendBtnStatusListener() {
//        if (isSNOk){
//            button_house.setTextColor(Color.parseColor("#FFFFFFFF"))
//            button_house.setOnClickListener {
//
//                sendRequest(
//                    ApplicationLevel.loadXHouse(),
//                    ApplicationLevel.loadYHouse()
//                )
//            }
//        } else {
//            button_house.setOnClickListener {
//                Toast.makeText(this, "Не введен серийный номер", Toast.LENGTH_LONG).show()
//            }
//            button_house.setTextColor(Color.parseColor("#512B58"))
//        }

        if (isGPSOk && isSNOk) {
            isStoreHouseLastClick = false

            button.text = "ОТПРАВИТЬ"
            button.isEnabled = true
            button.setTextColor(Color.parseColor("#FFFFFFFF"))


            button.setOnClickListener {
                sendRequest(lastLocation!!.latitude.toString(),
                    lastLocation!!.longitude.toString())
            }

        } else {
            button.setTextColor(Color.parseColor("#79BAC1"))

            button.setOnClickListener {}
            button.isEnabled = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle item selection
        return when (item.getItemId()) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        preambula_text.text = ApplicationLevel.loadPreambula()

        try {
        updateRucycler()
        } catch (e : Exception){
            Toast.makeText(this, "Пожалуйста, удалите приложение и установите его заново", Toast.LENGTH_LONG).show()
            e.printStackTrace()}
    }


    fun updateRucycler() {
        requests = ArrayList()
        notSendedRequests = mutableListOf()
        requests = getDataFromSharedPreferences()

        if (requests != null) {
            requests.forEach {
                if (!it.isSended) {
                    notSendedRequests!!.add(it)
                }
            }

            for (i in 0 until notSendedRequests!!.size){
                val isOk = AsyncHttp(
                    ApplicationLevel.loadServerUri(),
                    "380${ApplicationLevel.loadPhone()}",
                    notSendedRequests!![i]
                ).execute()

                if (isOk) {
                    shadow_ll.visibility = View.GONE
                    var current = requests.find {
                        it == notSendedRequests!![i]
                    }

                    current!!.isSended = true

                }
            }

            setDataFromSharedPreferences(requests)

            notSendedRequests = mutableListOf()

            if(requests != null){
                requests.forEach {
                    if (!it.isSended) {
                        notSendedRequests!!.add(it)
                    }
                }
            }

            if (notSendedRequests!!.size != 0) {
                rw.removeAllViews()
                adapter = MyAdapter(notSendedRequests!!, this)
                val linearLayoutManager = LinearLayoutManager(this)
                rw.layoutManager = linearLayoutManager as RecyclerView.LayoutManager?
                rw.adapter = adapter
                hidable_ll.visibility = View.VISIBLE
            } else {
                hidable_ll.visibility = View.GONE
            }
        }
    }

    fun sendRequest(x: String, y : String){

        shadow_ll.visibility = View.VISIBLE

        val request = Request(
            System.currentTimeMillis(),
            ApplicationLevel.loadPreambula() + "" + sn_edittext.text,
            x,
            y
        )

        if (requests == null){
            requests = ArrayList()
        }

        requests.add(request)
        setDataFromSharedPreferences(requests)

        val isOk = AsyncHttp(
            ApplicationLevel.loadServerUri(),
            "380${ApplicationLevel.loadPhone()}",
            request
        ).execute()

        if (isOk) {
            lastLocation = null
            gps_text.text = "GPS координаты пусты"
            sn_edittext.text.clear()
            gtp_image_status.setBackgroundResource(R.drawable.baseline_done_grey_18dp)
            isGPSOk = false
            isSNOk = false
            hidable_ll.visibility = View.VISIBLE
            Toast.makeText(this, "ОТПРАВЛЕНО УСПЕШНО!", Toast.LENGTH_LONG).show()
            shadow_ll.visibility = View.GONE
            requests.last().isSended = true
            setDataFromSharedPreferences(requests)
            updateRucycler()
        } else {
            Toast.makeText(
                this,
                "ОШИБКА ПРИ ОТПРАВКЕ СООБЩЕНИЯ! ПОСТАВЛЕНО В ОЧЕРЕДЬ!",
                Toast.LENGTH_LONG
            ).show()
            shadow_ll.visibility = View.GONE
            updateRucycler()
        }
    }
}
