package com.example.terralog.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.terralog.datos.entidades.Instrumento

@Dao
interface InstrumentoDao {
    @Insert
    suspend fun insertar(instrumento: Instrumento): Long

    @Query("SELECT * FROM instrumentos ORDER BY nombre")
    suspend fun todos(): List<Instrumento>
}