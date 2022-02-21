package pmdm.agarimo.controller

import pmdm.agarimo.entity.Users
import pmdm.agarimo.entity.Professional

data class Response(
    var professionals: List<Professional>? = null, // Lista de profesionales como respuesta
    var exception: Exception? = null, // Excepción en caso de error en respuesta
    var users: List<Users>? = null // Lista de clientes

)
