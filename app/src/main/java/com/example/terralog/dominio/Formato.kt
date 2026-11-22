package com.example.terralog.dominio

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object Formato {
    private val local = Locale.forLanguageTag("es-CL")

    fun fechaHora(millis: Long): String = SimpleDateFormat("dd-MM-yyyy HH:mm", local).format(Date(millis))

    fun fecha(millis: Long): String = SimpleDateFormat("dd-MM-yyyy", local).format(Date(millis))


    fun numero(valor: Double): String =
        DecimalFormat("#,##0.##", DecimalFormatSymbols(local)).format(valor)


    fun variacion(valor: Double): String {
        val texto = DecimalFormat("+0.0;-0.0", DecimalFormatSymbols(local)).format(valor)
        return "$texto %"
    }
}