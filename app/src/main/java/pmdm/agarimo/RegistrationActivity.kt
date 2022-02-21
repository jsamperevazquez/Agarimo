package pmdm.agarimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*
import pmdm.agarimo.entity.Users


class RegistrationActivity : AppCompatActivity() {
    private lateinit var rButton: Button
    private lateinit var users: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        users = Users()
        rButton = regButton
        takeData()
    }
    // Método para registrar clientes de la app en FireBase
    fun takeData() {
        val agarimo : DatabaseReference = FirebaseDatabase.getInstance("https://agarimoapp-default-rtdb.europe-west1.firebasedatabase.app").reference
        val clienteRef: DatabaseReference = agarimo.child("usuarios")
        // Setter a cliente
        rButton.setOnClickListener {
            users.let {
                it.name = nameInput.text.toString()
                it.lastName = lastNameInput.text.toString()
                it.email = emailInput.text.toString()
                it.password = passInput.text.toString()
            }
            var key =  clienteRef.push().key // Genero key a objeto en referencia fireBase
            if (key != null) {
                clienteRef.child(key).setValue(users) // Ingreso a referencia valores parámetros de cliente
            }

        }
    }
}