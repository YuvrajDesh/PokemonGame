package com.yuvrajdeshmukh.pokemongame

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yuvrajdeshmukh.pokemongame.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

     lateinit var mMap: GoogleMap

    private val USER_LOCATION_REQUEST_CODE = 100
    var PlayerLocation : Location? = null
    var oldLocationPlayer: Location? = null
     var locationManager :LocationManager? = null
     var locationlistener:LocationListener? = null
    var pokemonCharacters  : ArrayList<PokemonCharacter> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationlistener = PlayerLocationListener()
        requsestLocationPermission()
        initializePokemonCharacters()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
         //19.958505  75.315277
        // Add a marker in Sydney and move the camera

    }
    //ask permisssions
    private fun requsestLocationPermission()
    {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                USER_LOCATION_REQUEST_CODE)
                return
            }
        }
        accessUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==USER_LOCATION_REQUEST_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
              accessUserLocation()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    inner class PlayerLocationListener:LocationListener
    {
       constructor()
       {
           PlayerLocation = Location("MyProvider")
           PlayerLocation?.latitude = 0.0
           PlayerLocation?.longitude = 0.0



       }









        override fun onLocationChanged(upDatedLocation: Location) {
            PlayerLocation = upDatedLocation
        }


    }
    fun initializePokemonCharacters()
    {
        pokemonCharacters.add(PokemonCharacter("Hello, this is c1",
            "I'm powerful", R.drawable.c1, 1.651729,
            31.996134))
        pokemonCharacters.add(PokemonCharacter("Hello, this is c2",
            "I'm powerful", R.drawable.c2, 27.404523,
            29.647654))
        pokemonCharacters.add(PokemonCharacter("Hello, this is c3",
            "I'm powerful", R.drawable.c3, 10.492703,
            10.709112))
        pokemonCharacters.add(PokemonCharacter("Hello, this is c4",
            "I'm powerful", R.drawable.c4, 28.220750,
            1.898764))
    }
    private fun accessUserLocation()
    {

        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,1,2f,locationlistener!!
        )
        var newThread = NewThread()
        newThread.start()

    }
    inner class NewThread:Thread
    {
        constructor():super()
        {
         oldLocationPlayer = Location("MyProvider")
            oldLocationPlayer?.longitude = 0.0
            oldLocationPlayer?.latitude = 0.0
        }

        override fun run() {
            super.run()
            while(true)
            {
                if(oldLocationPlayer?.distanceTo(PlayerLocation)==0f)
                {
                    continue
                }
                oldLocationPlayer = PlayerLocation
                try{
                    runOnUiThread {
                        mMap.clear()
                        var pLocation = LatLng(PlayerLocation!!.latitude,PlayerLocation!!.longitude )
                        mMap.addMarker(MarkerOptions().position(pLocation).title("I am the player").snippet(
                            "Lets go"
                        ).icon(BitmapDescriptorFactory.fromResource(R.drawable.player)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pLocation))

                        for (pokemonCharcterIndex in 0.until(pokemonCharacters.size))
                        {
                            var pc = pokemonCharacters[pokemonCharcterIndex]
                            if(pc.isKilled == false)
                            {
                                var pcLocation = LatLng(pc.location!!.latitude, pc.location!!.longitude)
                                mMap.addMarker(MarkerOptions().position(pcLocation).title(pc.title).snippet(
                                    pc.message
                                ).icon(BitmapDescriptorFactory.fromResource(pc.icon!!)))
                                if (PlayerLocation!!.distanceTo(pc.location) < 1) {

                                    Toast.makeText(this@MapsActivity,
                                        "${pc.title} is eliminated",
                                        Toast.LENGTH_SHORT).show()
                                    pc.isKilled = true
                                    pokemonCharacters[pokemonCharcterIndex]= pc
                                }


                            }
                        }

                    }
                }catch (exception:Exception)
                {
                    exception.printStackTrace()
                }
            }



        }

    }

}