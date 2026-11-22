package com.example.terralog.dominio

import java.util.Calendar


object Validaciones {

    private val regexCorreo = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val regexTag = Regex("^[A-Za-z0-9][A-Za-z0-9/\\-]*$")
    const val MAX_TEXTO_LARGO = 200

    

    fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "Ingresa tu correo"
        !regexCorreo.matches(correo.trim()) -> "El correo no es válido"
        else -> null
    }

    fun validarClave(clave: String): String? = when {
        clave.isEmpty() -> "Ingresa tu contraseña"
        clave.length < 6 -> "Debe tener al menos 6 caracteres"
        else -> null
    }




    fun validarTag(tag: String, tagsExistentes: List<String>): String? {
        val limpio = tag.trim()
        return when {
            limpio.isEmpty() -> "Ingresa el TAG del equipo"
            limpio.length < 2 || limpio.length > 15 -> "Debe tener entre 2 y 15 caracteres"
            !regexTag.matches(limpio) -> "Solo letras, números, guion y barra (ej. TR-01)"
            tagsExistentes.any { it.equals(limpio, ignoreCase = true) } ->
                "Ya existe un equipo con ese TAG en la instalación"
            else -> null
        }
    }

    fun validarTipoEquipo(tipo: TipoEquipo?): String? =
        if (tipo == null) "Selecciona el tipo de equipo" else null

    fun validarInstalacion(instalacionId: Long?): String? =
        if (instalacionId == null) "Selecciona la instalación" else null

    fun validarTextoObligatorio(texto: String, nombre: String, minimo: Int = 2): String? = when {
        texto.isBlank() -> "Ingresa $nombre"
        texto.trim().length < minimo -> "Debe tener al menos $minimo caracteres"
        texto.trim().length > MAX_TEXTO_LARGO -> "Máximo $MAX_TEXTO_LARGO caracteres"
        else -> null
    }

    fun validarAnio(texto: String): String? {
        val anioActual = Calendar.getInstance().get(Calendar.YEAR)
        val anio = texto.trim().toIntOrNull() ?: return if (texto.isBlank()) "Ingresa el año" else "Ingresa un año válido"
        return if (anio < 1950 || anio > anioActual) "Debe estar entre 1950 y $anioActual" else null
    }



    
    fun aNumero(texto: String): Double? {
        val numero = texto.trim().replace(',', '.').toDoubleOrNull() ?: return null
        return if (numero.isNaN() || numero.isInfinite()) null else numero
    }

    fun validarNumero(texto: String, min: Double, max: Double, unidad: String): String? {
        if (texto.isBlank()) return "Campo obligatorio"
        val numero = aNumero(texto) ?: return "Ingresa un número válido"
        if (numero < min || numero > max) {
            return "Debe estar entre ${Formato.numero(min)} y ${Formato.numero(max)} $unidad".trim()
        }
        return null
    }

    fun validarTemperatura(texto: String): String? = validarNumero(texto, -20.0, 60.0, "°C")

    fun validarHumedad(texto: String): String? = validarNumero(texto, 0.0, 100.0, "%")

    fun validarMaxContactos(texto: String): String? = validarNumero(texto, 1.0, 10000.0, "µΩ")

    


    fun estaVencido(vencimiento: Long, ahora: Long): Boolean = ahora >= vencimiento + 86_400_000L

    fun validarInstrumento(vencimiento: Long?, ahora: Long): String? = when {
        vencimiento == null -> "Selecciona el instrumento utilizado"
        estaVencido(vencimiento, ahora) -> "Calibración vencida el ${Formato.fecha(vencimiento)}"
        else -> null
    }




    fun validarMotivo(texto: String): String? = when {
        texto.isBlank() -> "Escribe el motivo"
        texto.trim().length < 5 -> "Explícalo con al menos 5 caracteres"
        texto.trim().length > MAX_TEXTO_LARGO -> "Máximo $MAX_TEXTO_LARGO caracteres"
        else -> null
    }


    fun validarComentario(texto: String): String? =
        if (texto.trim().length > MAX_TEXTO_LARGO) "Máximo $MAX_TEXTO_LARGO caracteres" else null
}
