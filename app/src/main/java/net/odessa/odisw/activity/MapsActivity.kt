package net.odessa.odisw.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_maps.*
import net.odessa.odisw.R


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var x : String = ""
    var y : String = ""

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        x=intent.getStringExtra("latitude")!!
        y=intent.getStringExtra("longitude")!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        select_new_xy.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitude", x)
            intent.putExtra("longitude", y)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val currentPos = LatLng(x.toDouble(), y.toDouble())
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 20f))

        mMap.setOnCameraIdleListener {
            val midLatLng: LatLng = mMap.cameraPosition.target
            x= midLatLng.latitude.toString()
            y = midLatLng.longitude.toString()
        }
    }
}
