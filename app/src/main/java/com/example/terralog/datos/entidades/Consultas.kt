package com.example.terralog.datos.entidades

import androidx.room.Embedded

data class EquipoConInstalacion(
    @Embedded val equipo: Equipo,
    val instalacionNombre: String,
    val clienteNombre: String,
    val clienteId: Long
)

data class InstalacionConCliente(
    @Embedded val instalacion: Instalacion,
    val clienteNombre: String
)