package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "equipos",
    foreignKeys = [
        ForeignKey(
            entity = Instalacion::class,
            parentColumns = ["id"],
            childColumns = ["instalacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["instalacionId", "tag"], unique = true)]
)
data class Equipo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val instalacionId: Long,
    val tag: String,
    val tipo: String,
    val marca: String,
    val nroSerie: String,
    val anio: Int,
    val datosPlaca: String,
    val maxContactos: Double? = null,
    val rutaFotoPlaca: String? = null
)
