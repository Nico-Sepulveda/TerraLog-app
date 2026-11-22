package com.example.terralog.dominio

enum class EstadoProtocolo(val nombre: String) {
    BORRADOR("Borrador"),
    REGISTRADO_EN_TERRENO("Registrado en terreno"),
    OBSERVADO("Observado"),
    FORMALIZADO("Formalizado"),
    ANULADO("Anulado");

    fun nombreParaCliente(): String = when (this) {
        REGISTRADO_EN_TERRENO, OBSERVADO -> "Pendiente de formalización"
        else -> nombre
    }

    companion object {
        fun desde(texto: String): EstadoProtocolo = entries.firstOrNull { it.name == texto } ?: BORRADOR

        val visiblesParaCliente = listOf(REGISTRADO_EN_TERRENO, OBSERVADO, FORMALIZADO)
    }
}