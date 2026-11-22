package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "pruebas",
    foreignKeys = [
        ForeignKey(
            entity = Protocolo::class,
            parentColumns = ["id"],
            childColumns = ["protocoloId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("protocoloId")]
)
data class Prueba(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val protocoloId: Long,
    val tipoPrueba: String,
    val instrumentoId: Long? = null,
    val realizada: Boolean = true,
    val motivoNoRealizada: String? = null,
    val resultado: String? = null,
    val comentario: String? = null
)