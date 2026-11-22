package com.example.terralog.dominio

data class CampoMedicion(
    val clave: String,
    val nombre: String,
    val unidad: String,
    val min: Double,
    val max: Double,
    val soloSiOpera: Boolean = false
)

enum class TipoPrueba(
    val nombre: String,
    val campos: List<CampoMedicion>,
    val principales: List<String>,
    val resultadoManual: Boolean = false
) {
    AISLACION_10_MIN(
        "Resistencia de aislación (1 y 10 min)",
        listOf(
            CampoMedicion("lectura1", "Lectura a 1 min", "MΩ", 0.1, 1000000.0),
            CampoMedicion("lectura10", "Lectura a 10 min", "MΩ", 0.1, 1000000.0),
            CampoMedicion("tension", "Tensión de prueba", "kV", 0.1, 15.0)
        ),
        listOf("lectura1")
    ),
    AISLACION_1_MIN(
        "Resistencia de aislación (1 min)",
        listOf(
            CampoMedicion("lectura1", "Lectura a 1 min", "MΩ", 0.1, 1000000.0),
            CampoMedicion("tension", "Tensión de prueba", "kV", 0.1, 15.0)
        ),
        listOf("lectura1")
    ),
    TTR(
        "Relación de transformación (TTR)",
        listOf(
            CampoMedicion("relacionTeorica", "Relación teórica", "", 0.1, 1000.0),
            CampoMedicion("relacionMedida", "Relación medida", "", 0.1, 1000.0),
            CampoMedicion("posicion", "Posición del cambiador", "", 1.0, 33.0)
        ),
        listOf("relacionMedida")
    ),
    DEVANADOS(
        "Resistencia de devanados",
        listOf(
            CampoMedicion("faseA", "Fase A", "mΩ", 0.1, 100000.0),
            CampoMedicion("faseB", "Fase B", "mΩ", 0.1, 100000.0),
            CampoMedicion("faseC", "Fase C", "mΩ", 0.1, 100000.0)
        ),
        listOf("faseA", "faseB", "faseC"),
        resultadoManual = true
    ),
    CONTACTOS(
        "Resistencia de contactos",
        listOf(
            CampoMedicion("polo1", "Polo 1", "µΩ", 1.0, 10000.0),
            CampoMedicion("polo2", "Polo 2", "µΩ", 1.0, 10000.0),
            CampoMedicion("polo3", "Polo 3", "µΩ", 1.0, 10000.0)
        ),
        listOf("polo1", "polo2", "polo3")
    ),
    OPERACION_RELE(
        "Prueba de operación del relé",
        listOf(
            CampoMedicion("ajuste", "Ajuste de corriente de operación", "A", 0.1, 100.0),
            CampoMedicion("corrienteMedida", "Corriente medida de operación", "A", 0.1, 100.0, soloSiOpera = true),
            CampoMedicion("tiempoEsperado", "Tiempo esperado", "ms", 1.0, 10000.0),
            CampoMedicion("tiempoMedido", "Tiempo medido", "ms", 1.0, 10000.0, soloSiOpera = true)
        ),
        listOf("tiempoMedido")
    );

    companion object {
        fun desde(texto: String): TipoPrueba? = entries.firstOrNull { it.name == texto }
    }
}