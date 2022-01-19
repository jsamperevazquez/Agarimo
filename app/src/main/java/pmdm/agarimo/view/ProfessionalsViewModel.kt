package pmdm.agarimo.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import pmdm.agarimo.repository.ProfessionalsRepository

class ProfessionalsViewModel(
    private val repository: ProfessionalsRepository = ProfessionalsRepository() // Para poder usar metodo de repository
) : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    // Comienza a ejecutarse cuando se activa LiveData, y se cancela automáticamente después de un tiempo de espera configurable cuando LiveData se vuelve inactivo:
    //Dispatchers.IO, está optimizado para realizar E/S de disco o red fuera del subproceso principal.
    val responseLiveData = liveData(Dispatchers.IO){
        emit(repository.dataConnect()) // También puedes emitir varios valores desde el bloque. Cada llamada a emit() suspende la ejecución del bloque hasta que se establezca el valor LiveData en el subproceso principal.
    }
}