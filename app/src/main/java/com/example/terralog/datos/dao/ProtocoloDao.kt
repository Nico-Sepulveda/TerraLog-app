package com.example.terralog.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.terralog.datos.entidades.Protocolo
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocoloDao {
    @Insert
    suspend fun insertar(protocolo: Protocolo): Long

    @Update
    suspend fun actualizar(protocolo: Protocolo)

    @Query("SELECT * FROM protocolos WHERE id = :id")
    suspend fun obtener(id: Long): Protocolo?

    
    @Query("SELECT * FROM protocolos WHERE id = :id")
    fun observar(id: Long): Flow<Protocolo?>

    
    @Query("SELECT * FROM protocolos WHERE equipoId = :equipoId ORDER BY fechaHora DESC")
    fun deEquipo(equipoId: Long): Flow<List<Protocolo>>

    
    @Query("SELECT * FROM protocolos WHERE equipoId = :equipoId AND estado IN (:estados) ORDER BY fechaHora DESC")
    fun deEquipoConEstados(equipoId: Long, estados: List<String>): Flow<List<Protocolo>>

    @Query("SELECT COUNT(*) FROM protocolos")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM protocolos WHERE numero = :numero")
    suspend fun contarConNumero(numero: String): Int
}