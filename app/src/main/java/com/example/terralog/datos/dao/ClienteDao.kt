package com.example.terralog.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.terralog.datos.entidades.Cliente

@Dao
interface ClienteDao {
    @Insert
    suspend fun insertar(cliente: Cliente): Long
}