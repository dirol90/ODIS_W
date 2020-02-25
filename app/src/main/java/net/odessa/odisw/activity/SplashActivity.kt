package net.odessa.odisw.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*
import net.odessa.odisw.ApplicationLevel
import net.odessa.odisw.ApplicationLevel.loadPhone
import net.odessa.odisw.ApplicationLevel.savePhone
import net.odessa.odisw.R


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        if(System.currentTimeMillis() < 1583532001000) {
            et_phone.text = loadPhone().toEditable()
            if (et_phone.text.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }

            start_btn.setOnClickListener {
                if (et_phone.text.length != 9) {
                    Toast.makeText(
                        this,
                        "Не верный номер телефона, проверьте его повторно!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    savePhone(et_phone.text.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }

            }
//        } else {
//            Toast.makeText(this, "ДЕМО ДОСТУП ОКОНЧЕН", Toast.LENGTH_LONG).show()
//        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}
