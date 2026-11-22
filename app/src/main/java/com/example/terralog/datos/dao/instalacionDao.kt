package com.example.terralog.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.terralog.datos.entidades.Instalacion
import com.example.terralog.datos.entidades.InstalacionConCliente
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalacionDao {
    @Insert
    suspend fun insertar(instalacion: Instalacion): Long

    @Query(
        "SELECT i.*, c.razonSocial AS clienteNombre FROM instalaciones i " +
                "INNER JOIN clientes c ON c.id = i.clienteId ORDER BY i.nombre"
    )
    fun todasConCliente(): Flow<List<InstalacionConCliente>>
}