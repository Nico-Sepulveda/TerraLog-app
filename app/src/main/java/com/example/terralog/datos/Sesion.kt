package com.example.terralog.datos

import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.datos.entidades.Prueba
import com.example.terralog.datos.entidades.Usuario
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Resultado
import com.example.terralog.dominio.Rol
import com.example.terralog.dominio.TipoEquipo
import com.example.terralog.dominio.TipoPrueba


object Sesion {
    var usuario: Usuario? = null
        private set

    fun iniciar(nuevo: Usuario) {
        usuario = nuevo
    }

    fun cerrar() {
        usuario = null
    }

    fun rol(): Rol = usuario?.let { Rol.desde(it.rol) } ?: Rol.CLIENTE
}


fun Usuario.rolEnum(): Rol = Rol.desde(rol)

fun Equipo.tipoEnum(): TipoEquipo? = TipoEquipo.desde(tipo)

fun Protocolo.estadoEnum(): EstadoProtocolo = EstadoProtocolo.desde(estado)

fun Protocolo.resultadoGlobalEnum(): Resultado? = Resultado.desde(resultadoGlobal)

fun Prueba.tipoEnum(): TipoPrueba? = TipoPrueba.desde(tipoPrueba)

fun Prueba.resultadoEnum(): Resultado? = Resultado.desde(resultado)
