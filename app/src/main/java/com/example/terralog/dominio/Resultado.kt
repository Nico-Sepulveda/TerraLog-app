package com.example.terralog.dominio

enum class Resultado(val nombre: String, val gravedad: Int) {
    APROBADO("Aprobado", 0),
    OBSERVADO("Observado", 1),
    RECHAZADO("Rechazado", 2);

    companion object {
        fun desde(texto: String?): Resultado? = entries.firstOrNull { it.name == texto }

        
        fun peor(lista: List<Resultado>): Resultado? = lista.maxByOrNull { it.gravedad }
    }
}