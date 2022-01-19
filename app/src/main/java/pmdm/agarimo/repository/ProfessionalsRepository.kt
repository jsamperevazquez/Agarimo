package pmdm.agarimo.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import pmdm.agarimo.controller.Response
import pmdm.agarimo.entity.Professional
import kotlin.Exception

class ProfessionalsRepository(
    // Establezco la referencia a mi base datos RealTime !!!!IMPORTANTE DARLE LA REFERNCIA CON getInstance()
    private val agarimo: DatabaseReference = FirebaseDatabase.getInstance("https://agarimoapp-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val professionalRef: DatabaseReference = agarimo.child("profesionales")
) {
    //Función suspend para poder usar corutinas
     suspend fun dataConnect(): Response {

         val response = Response() // Instancio un Response() para poder usar patrón diseño
         try {
             // Obtenemos la datos de nuestra referncia a BD con get de forma asíncrona con await y mapeamos:
             response.professionals = professionalRef.get().await().children.map { snapshot ->
                 snapshot.getValue(Professional::class.java)!!
             }
         }catch (ex: Exception){
             response.exception = ex
         }
         return response
    }
}