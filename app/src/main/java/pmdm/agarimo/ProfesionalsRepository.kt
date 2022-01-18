package pmdm.agarimo

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.Exception

class ProfesionalsRepository(
    private val agarimo: DatabaseReference = FirebaseDatabase.getInstance("https://agarimoapp-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val profesionalRef: DatabaseReference = agarimo.child("profesionales")
) {
     suspend fun dataConnect(): Response {

         val response = Response()
         try {
             response.profesionals = profesionalRef.get().await().children.map { snapshot ->
                 snapshot.getValue(Profesional::class.java)!!
             }
         }catch (ex: Exception){
             response.exception = ex
         }
         return response
    }
}