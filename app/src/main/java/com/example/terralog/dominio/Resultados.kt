package com.example.terralog.dominio

import kotlin.math.abs


data class ValorCalculado(
    val clave: String,
    val nombre: String,
    val valor: Double,
    val unidad: String
)


data class ResultadoPrueba(
    val resultado: Resultado?,
    val calculados: List<ValorCalculado>
)


object Resultados {

    fun calcular(
        tipo: TipoPrueba,
        v: Map<String, Double>,
        maxContactos: Double?
    ): ResultadoPrueba = when (tipo) {
        TipoPrueba.AISLACION_10_MIN -> {
            val lectura1 = v.getValue("lectura1")
            val ip = v.getValue("lectura10") / lectura1
            val resultado = when {
                lectura1 < 100.0 || ip < 1.0 -> Resultado.RECHAZADO
                ip < 2.0 -> Resultado.OBSERVADO
                else -> Resultado.APROBADO
            }
            ResultadoPrueba(resultado, listOf(ValorCalculado("ip", "Índice de polarización", ip, "")))
        }

        TipoPrueba.AISLACION_1_MIN -> {
            val resultado = if (v.getValue("lectura1") < 100.0) Resultado.RECHAZADO else Resultado.APROBADO
            ResultadoPrueba(resultado, emptyList())
        }

        TipoPrueba.TTR -> {
            val teorica = v.getValue("relacionTeorica")
            val error = abs(v.getValue("relacionMedida") - teorica) / teorica * 100
            val resultado = when {
                error <= 0.5 -> Resultado.APROBADO
                error <= 1.0 -> Resultado.OBSERVADO
                else -> Resultado.RECHAZADO
            }
            ResultadoPrueba(resultado, listOf(ValorCalculado("error", "Error de relación", error, "%")))
        }

        TipoPrueba.DEVANADOS -> ResultadoPrueba(null, emptyList())

        TipoPrueba.CONTACTOS -> {
            if (maxContactos == null || maxContactos <= 0.0) {
                ResultadoPrueba(null, emptyList())
            } else {
                val mayor = maxOf(v.getValue("polo1"), v.getValue("polo2"), v.getValue("polo3"))
                val porcentaje = mayor / maxContactos * 100
                val resultado = when {
                    porcentaje <= 100.0 -> Resultado.APROBADO
                    porcentaje <= 120.0 -> Resultado.OBSERVADO
                    else -> Resultado.RECHAZADO
                }
                ResultadoPrueba(resultado, listOf(ValorCalculado("porcentajeMaximo", "Mayor polo vs. máximo de placa", porcentaje, "%")))
            }
        }

        TipoPrueba.OPERACION_RELE -> {
            val opera = v["opera"] != 0.0
            if (!opera) {
                ResultadoPrueba(Resultado.RECHAZADO, emptyList())
            } else {
                val esperado = v.getValue("tiempoEsperado")
                val diferencia = abs(v.getValue("tiempoMedido") - esperado) / esperado * 100
                val resultado = when {
                    diferencia <= 5.0 -> Resultado.APROBADO
                    diferencia <= 10.0 -> Resultado.OBSERVADO
                    else -> Resultado.RECHAZADO
                }
                ResultadoPrueba(resultado, listOf(ValorCalculado("diferenciaTiempo", "Diferencia de tiempo", diferencia, "%")))
            }
        }
    }
}
