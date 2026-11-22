package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "mediciones",
    foreignKeys = [
        ForeignKey(
            entity = Prueba::class,
            parentColumns = ["id"],
            childColumns = ["pruebaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pruebaId")]
)
data class Medicion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pruebaId: Long,
    val clave: String,
    val nombre: String,
    val valor: Double,
    val unidad: String,
    val calculado: Boolean = false
)
