package pmdm.agarimo

data class Response(
    var profesionals: List<Profesional>? = null,
    var exception: Exception? = null
)
