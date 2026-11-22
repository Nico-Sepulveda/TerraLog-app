package com.example.terralog.dominio

import kotlin.math.abs


object Variacion {
    const val UMBRAL_SIGNIFICATIVA = 25.0


    fun porcentual(actual: Double, anterior: Double): Double? {
        if (anterior == 0.0) return null
        return (actual - anterior) / anterior * 100
    }


    fun esSignificativa(variacion: Double): Boolean = abs(variacion) > UMBRAL_SIGNIFICATIVA
}