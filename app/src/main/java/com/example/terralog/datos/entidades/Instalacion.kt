package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "instalaciones",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class Instalacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val nombre: String,
    val comuna: String,
    val observacionesAcceso: String
)