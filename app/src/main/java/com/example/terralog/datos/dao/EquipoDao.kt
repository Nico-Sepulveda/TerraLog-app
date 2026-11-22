package com.example.terralog.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.EquipoConInstalacion
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Insert
    suspend fun insertar(equipo: Equipo): Long

    
    @Query(
        "SELECT e.*, i.nombre AS instalacionNombre, c.razonSocial AS clienteNombre, c.id AS clienteId " +
                "FROM equipos e INNER JOIN instalaciones i ON e.instalacionId = i.id " +
                "INNER JOIN clientes c ON i.clienteId = c.id ORDER BY e.tag"
    )
    fun todos(): Flow<List<EquipoConInstalacion>>

    
    @Query(
        "SELECT e.*, i.nombre AS instalacionNombre, c.razonSocial AS clienteNombre, c.id AS clienteId " +
                "FROM equipos e INNER JOIN instalaciones i ON e.instalacionId = i.id " +
                "INNER JOIN clientes c ON i.clienteId = c.id WHERE c.id = :clienteId ORDER BY e.tag"
    )
    fun deCliente(clienteId: Long): Flow<List<EquipoConInstalacion>>

    @Query(
        "SELECT e.*, i.nombre AS instalacionNombre, c.razonSocial AS clienteNombre, c.id AS clienteId " +
                "FROM equipos e INNER JOIN instalaciones i ON e.instalacionId = i.id " +
                "INNER JOIN clientes c ON i.clienteId = c.id WHERE e.id = :id"
    )
    suspend fun obtener(id: Long): EquipoConInstalacion?

    @Query("SELECT tag FROM equipos WHERE instalacionId = :instalacionId")
    suspend fun tagsDeInstalacion(instalacionId: Long): List<String>
}