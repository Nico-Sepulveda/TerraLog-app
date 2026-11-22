package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instrumentos")
data class Instrumento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val marcaModelo: String,
    val nroSerie: String,
    val vencimientoCalibracion: Long
)