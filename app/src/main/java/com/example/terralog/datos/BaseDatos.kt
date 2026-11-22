package com.example.terralog.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.terralog.datos.dao.ClienteDao
import com.example.terralog.datos.dao.EquipoDao
import com.example.terralog.datos.dao.InstalacionDao
import com.example.terralog.datos.dao.InstrumentoDao
import com.example.terralog.datos.dao.ProtocoloDao
import com.example.terralog.datos.dao.PruebaDao
import com.example.terralog.datos.dao.UsuarioDao
import com.example.terralog.datos.entidades.Cliente
import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.Instalacion
import com.example.terralog.datos.entidades.Instrumento
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.datos.entidades.Prueba
import com.example.terralog.datos.entidades.Usuario


@Database(
    entities = [
        Usuario::class, Cliente::class, Instalacion::class, Equipo::class,
        Instrumento::class, Protocolo::class, Prueba::class, Medicion::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BaseDatos : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun instalacionDao(): InstalacionDao
    abstract fun equipoDao(): EquipoDao
    abstract fun instrumentoDao(): InstrumentoDao
    abstract fun protocoloDao(): ProtocoloDao
    abstract fun pruebaDao(): PruebaDao

    companion object {
        @Volatile
        private var instancia: BaseDatos? = null


        fun obtener(contexto: Context): BaseDatos {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDatos::class.java,
                    "terralog.db"
                ).build().also { instancia = it }
            }
        }
    }
}
