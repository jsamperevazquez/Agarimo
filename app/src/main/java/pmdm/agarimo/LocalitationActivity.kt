package pmdm.agarimo

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_localitation.*
import java.io.IOException

class LocalitationActivity : AppCompatActivity(), OnMapReadyCallback {

    //Localización Mapa
    private lateinit var map: GoogleMap
    private lateinit var mark: MarkerOptions

    // Base de datos (Usar modelo)
    private lateinit var viewModel: ProfesionalsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localitation)
        viewModel = ViewModelProvider(this).get(ProfesionalsViewModel::class.java)
        createMapFragment() // LLama a la función que inicializa el fragmento de layout
        val button: Button = findViewById(R.id.profButton)
        button.setOnClickListener { getResponseProfesionals() }
        val connectedRef = Firebase.database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d(TAG, "CONECTADO COÑO")
                } else {
                    Log.d(TAG, "not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled")
            }
        })

    }


    //Método de la interfaz onMapReadyCallback
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    // función encargada de inicializar el fragment que hemos creado en el layout

    private fun createMapFragment() {
        // Variable que decimos a supportFragmentManager que busque un fragment que tenga una id llamada fragmentMap, que será la id del fragment que añadimos en activity_main.xml
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this) //la inicializamos con la función getMapAsync(this).
    }


    // Función para comprobar permiso de localización
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED // Si permisos están activados o no.

    // Función para comprobar si se ha iniciado mapa
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermision()
        }
    }

    // Función para solicitar los permisos
    companion object {
        const val REQUEST_CODE_LOCATION = 0 // Código para saber si es nuestro permiso el aceptado
    }

    private fun requestLocationPermision() {
        // Si entra en if ya se habrían rechazado permisos
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                this,
                " Please go to settings and accept the permissions",
                Toast.LENGTH_SHORT
            ).show()
        } else { // Si entra en else nunca se han pedido permisos, se hace a traves de ActivityCompact.requestPermisions pasando activity (this), permiso que queremos que acepte, y código creado.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "To activate the location go to settings and accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    // Función para que no rompa app en caso anulación de perms en settings
    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return // Si el mapa ha sido cargado
        if (!isPermissionsGranted()) { // Si los permisos están activos
            map.isMyLocationEnabled = false // Si no, desactivamos localización en tiempo real
            Toast.makeText(
                this,
                "To activate the location go to settings and accept the permissions",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Método para buscar dirección proporcionada por usuario

    public fun searchStreet(view: View) {
        var street =
            streetSearch.text.toString().trim() // recojo el valor introducido por el usuario
        var addresList: List<Address>? = null // creo una lista de Address

        if (street == null || street == "") { // Si el entry está vacío
            Toast.makeText(applicationContext, "Provide location please", Toast.LENGTH_LONG).show()
        } else {
            val geoCoder =
                Geocoder(this) // geocoder para convertir la latitud y la longitud en información de dirección detallada
            try {
                addresList = geoCoder.getFromLocationName(
                    street,
                    1
                ) //Asigno a mi lista de Address geocoder para cambiar dirección a latitud y locngitud

            } catch (e: IOException) {
                e.printStackTrace()
            }

            val address =
                addresList!![0] // Creo una dirección y le asigno el valor Address de mi único valor de la lista
            val latLng = LatLng(
                address.latitude,
                address.longitude
            ) // Recojo la latitud y longitud de mi dirección

            // Creo una marca en el mapa con la latitud y longitud recogida y como título la calle
            map.addMarker(MarkerOptions().position(latLng).title(street))

            // Creo una animación de Cámara con un UpdateFactory para hacer zoom a la latitud y longitud pasadas
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))


            /*PERMISOS REQUERIDOS EN MANIFEST:
                android.permission.ACCESS_FINE_LOCATION
                android.permission.ACCESS_COARSE_LOCATION
                android.permission.INTERNET
             */


        }
    }


    fun getResponseProfesionals() {
        viewModel.responseLiveData.observe(this, {
            markResults(it)
        })

    }

    private fun markResults(response: Response) {
        var lat = 0.00
        var long = 0.00
        var profesionalMark = LatLng(0.00, 0.00)
        response.profesionals?.let { profesionals ->
            profesionals.forEach { profesional ->
                profesional.lat?.let {
                    lat = it
                }
                profesional.long?.let {
                    long = it
                }
                profesionalMark = LatLng(lat, long)
                map.addMarker(
                    MarkerOptions().position(profesionalMark).title("Professional")
                )
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(profesionalMark, 18f),
                    4000,
                    null
                )
            }
        }
        response.exception?.let { exception ->
            exception.message?.let {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
            }
        }
    }
}

