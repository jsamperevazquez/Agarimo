package pmdm.agarimo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import kotlin.properties.Delegates

class AuthenticationActivity : AppCompatActivity() {
    //Autenticación con FireBase
    private val TAG = "LoginActivity"

    //Variables globales
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var butSup: Button
    private lateinit var etPassword: EditText
    private lateinit var mProgressBar: ProgressDialog
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initialise()

    }

    //Método para inicializar nuestros elementos de diseño y autenticación de FireBase
    private fun initialise() {
        etEmail = editTextTextEmailAddress
        etPassword = editTextTextPassword
        mProgressBar = ProgressDialog(this)
        butSup = buttonSingIn
        auth = FirebaseAuth.getInstance()
    }

    // Iniciar sesión con firebase
    private fun loginUser() {
        // Creamos una variable e instanciamos un objeto intent al que pasamos nuestra Activity localitation
        val intentLocation = Intent(this, LocalitationActivity::class.java)

        // Obtenemos usuario y contraseña
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        // Verificamos que los campos no están vacíos
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && password.length >= 6) {


            // Iniciamos sesion con el método sigIn y enviamos usuario y contraseña
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                // Verificamos que la tarea se ejecutó correctamente
                    task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Si se inició correctamente la sesión, cargaremos el mapa con fragmento
                    Toast.makeText(
                        applicationContext,
                        "Wellcome to Agarimo",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intentLocation)
                } else {
                    // Sacamos toast
                    Toast.makeText(
                        applicationContext,
                        "Authentication failed  $email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()

    }

    fun login(view: View) {
        loginUser()
    }
}