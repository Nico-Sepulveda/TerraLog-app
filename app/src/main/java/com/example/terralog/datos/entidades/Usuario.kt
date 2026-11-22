package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val rut: String,
    val correo: String,
    val clave: String,
    val rol: String,
    val activo: Boolean = true,
    val clienteId: Long? = null
)
