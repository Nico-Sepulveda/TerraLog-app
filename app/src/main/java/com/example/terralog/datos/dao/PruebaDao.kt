package com.example.terralog.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.entidades.Prueba
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {
    @Insert
    suspend fun insertar(prueba: Prueba): Long

    @Insert
    suspend fun insertarMediciones(mediciones: List<Medicion>)

    @Query("SELECT * FROM pruebas WHERE protocoloId = :protocoloId ORDER BY id")
    fun observarDeProtocolo(protocoloId: Long): Flow<List<Prueba>>

    @Query("SELECT * FROM pruebas WHERE protocoloId = :protocoloId")
    suspend fun deProtocolo(protocoloId: Long): List<Prueba>

    @Query("SELECT * FROM mediciones WHERE pruebaId = :pruebaId ORDER BY id")
    suspend fun medicionesDe(pruebaId: Long): List<Medicion>

    
    @Query(
        "SELECT pr.* FROM pruebas pr INNER JOIN protocolos p ON pr.protocoloId = p.id " +
                "WHERE p.equipoId = :equipoId AND pr.tipoPrueba = :tipoPrueba AND pr.realizada = 1 " +
                "AND p.estado != 'ANULADO' AND p.fechaHora < :fecha ORDER BY p.fechaHora DESC LIMIT 1"
    )
    suspend fun pruebaAnterior(equipoId: Long, tipoPrueba: String, fecha: Long): Prueba?
}
