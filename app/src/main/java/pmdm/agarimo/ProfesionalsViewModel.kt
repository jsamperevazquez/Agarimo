package pmdm.agarimo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class ProfesionalsViewModel(
    private val repository: ProfesionalsRepository = ProfesionalsRepository()
) : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    val responseLiveData = liveData(Dispatchers.IO){
        emit(repository.dataConnect())
    }
}