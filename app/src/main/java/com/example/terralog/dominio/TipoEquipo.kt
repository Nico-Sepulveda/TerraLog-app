package com.example.terralog.dominio

enum class ClaseEquipo(val nombre: String) {
    PRIMARIO("Primario"),
    SECUNDARIO("Secundario")
}


enum class TipoEquipo(
    val nombre: String,
    val clase: ClaseEquipo,
    val pruebas: List<TipoPrueba>
) {
    TRANSFORMADOR(
        "Transformador", ClaseEquipo.PRIMARIO,
        listOf(TipoPrueba.AISLACION_10_MIN, TipoPrueba.TTR, TipoPrueba.DEVANADOS)
    ),
    INTERRUPTOR(
        "Interruptor", ClaseEquipo.PRIMARIO,
        listOf(TipoPrueba.AISLACION_1_MIN, TipoPrueba.CONTACTOS)
    ),
    CELDA_MT(
        "Celda MT", ClaseEquipo.PRIMARIO,
        listOf(TipoPrueba.AISLACION_1_MIN, TipoPrueba.CONTACTOS)
    ),
    CABLE_PODER(
        "Cable de poder", ClaseEquipo.PRIMARIO,
        listOf(TipoPrueba.AISLACION_10_MIN)
    ),
    RELE_PROTECCION(
        "Relé de protección", ClaseEquipo.SECUNDARIO,
        listOf(TipoPrueba.OPERACION_RELE)
    );


    val usaContactos: Boolean get() = pruebas.contains(TipoPrueba.CONTACTOS)

    companion object {
        fun desde(texto: String): TipoEquipo? = entries.firstOrNull { it.name == texto }
    }
}
