package pmdm.agarimo.repository

import android.util.Log
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import pmdm.agarimo.controller.Response
import pmdm.agarimo.entity.Professional
import kotlin.Exception

class ProfessionalsRepository(
    // Establezco la referencia a mi base datos RealTime !!!!IMPORTANTE DARLE LA REFERNCIA CON getInstance()
    private val agarimo: DatabaseReference = FirebaseDatabase.getInstance("https://agarimoapp-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val professionalRef: DatabaseReference = agarimo.child("profesionales")
) {
    //Función para mapear la devolución de ref a fireBase a clase Profesional
     fun dataConnect(): Response {
        val response = Response() // Instancio un Response() para poder usar patrón diseño
         try {
             val profListener = object : ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                  response.professionals = snapshot.children.map { snapshot ->
                      snapshot.getValue(Professional::class.java)!!
                  }
                 }

                 override fun onCancelled(error: DatabaseError) {
                     Log.w("BD", error.message)
                 }
             }
             professionalRef.addValueEventListener(profListener)

         }catch (ex: Exception){
             response.exception = ex
         }
         return response
    }
}