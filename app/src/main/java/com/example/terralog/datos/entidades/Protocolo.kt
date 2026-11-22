package com.example.terralog.datos.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "protocolos",
    foreignKeys = [
        ForeignKey(
            entity = Equipo::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("equipoId"), Index(value = ["numero"], unique = true)]
)
data class Protocolo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numero: String,
    val equipoId: Long,
    val tecnicoId: Long,
    val fechaHora: Long,
    val temperatura: Double,
    val humedad: Double,
    val estado: String,
    val resultadoGlobal: String? = null,
    val motivo: String? = null,
    val comentarioIngeniero: String? = null,
    val formalizadoPorId: Long? = null,
    val fechaFormalizacion: Long? = null
)
