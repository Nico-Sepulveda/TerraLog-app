package com.example.terralog.datos

import android.content.Context
import androidx.room.withTransaction
import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.EquipoConInstalacion
import com.example.terralog.datos.entidades.InstalacionConCliente
import com.example.terralog.datos.entidades.Instrumento
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.datos.entidades.Prueba
import com.example.terralog.datos.entidades.Usuario
import com.example.terralog.dominio.Resultado
import com.example.terralog.dominio.Rol
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class Repositorio private constructor(private val bd: BaseDatos) {

    private val usuarioDao = bd.usuarioDao()
    private val equipoDao = bd.equipoDao()
    private val protocoloDao = bd.protocoloDao()
    private val pruebaDao = bd.pruebaDao()

    suspend fun sembrarDatosSiVacio() {
        if (usuarioDao.contar() == 0) DatosEjemplo.sembrar(bd)
    }

    suspend fun iniciarSesion(correo: String, clave: String): Usuario? =
        try {
            Api.usuarios.iniciarSesion(mapOf("correo" to correo, "clave" to clave))
        } catch (e: Exception) {
            null
        }

    suspend fun restablecerClave(correo: String, nuevaClave: String): Boolean =
        try {
            Api.usuarios.restablecer(mapOf("correo" to correo, "nuevaClave" to nuevaClave)).isSuccessful
        } catch (e: Exception) {
            false
        }

    suspend fun obtenerUsuario(id: Long): Usuario? = usuarioDao.obtener(id)

    private suspend fun armar(equipos: List<Equipo>): List<EquipoConInstalacion> {
        val instalaciones = Api.clientes.instalaciones().associateBy { it.id }
        val clientes = Api.clientes.clientes().associateBy { it.id }
        return equipos.mapNotNull { e ->
            val inst = instalaciones[e.instalacionId] ?: return@mapNotNull null
            EquipoConInstalacion(e, inst.nombre, clientes[inst.clienteId]?.razonSocial ?: "", inst.clienteId)
        }
    }

    fun equiposParaUsuario(usuario: Usuario): Flow<List<EquipoConInstalacion>> = flow {
        val clienteId = usuario.clienteId
        while (true) {
            val lista = try {
                val equipos = if (Rol.desde(usuario.rol) == Rol.CLIENTE && clienteId != null) Api.equipos.deCliente(clienteId) else Api.equipos.todos()
                armar(equipos)
            } catch (e: Exception) {
                null
            }
            if (lista != null) emit(lista)
            delay(3000)
        }
    }

    suspend fun obtenerEquipo(id: Long): EquipoConInstalacion? =
        try { armar(listOf(Api.equipos.equipo(id))).firstOrNull() } catch (e: Exception) { null }

    suspend fun tagsDeInstalacion(instalacionId: Long): List<String> =
        try { Api.equipos.deInstalacion(instalacionId).map { it.tag } } catch (e: Exception) { emptyList() }

    suspend fun registrarEquipo(equipo: Equipo): Long {
        val creado = Api.equipos.crear(
            mapOf(
                "instalacionId" to equipo.instalacionId,
                "tag" to equipo.tag,
                "tipo" to equipo.tipo,
                "marca" to equipo.marca,
                "nroSerie" to equipo.nroSerie,
                "anio" to equipo.anio,
                "datosPlaca" to equipo.datosPlaca,
                "maxContactos" to equipo.maxContactos
            )
        )
        runCatching { equipoDao.insertar(equipo.copy(id = creado.id)) }
        return creado.id
    }

    fun instalaciones(): Flow<List<InstalacionConCliente>> = flow {
        while (true) {
            val lista = try {
                val clientes = Api.clientes.clientes().associateBy { it.id }
                Api.clientes.instalaciones().map { InstalacionConCliente(it, clientes[it.clienteId]?.razonSocial ?: "") }
            } catch (e: Exception) {
                null
            }
            if (lista != null) emit(lista)
            delay(3000)
        }
    }

    suspend fun instrumentos(): List<Instrumento> =
        try {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            Api.instrumentos.todos().map {
                Instrumento(it.id, it.nombre, it.marcaModelo, it.nroSerie, formato.parse(it.vencimientoCalibracion)!!.time)
            }
        } catch (e: Exception) {
            emptyList()
        }

    private fun fechaALong(texto: String): Long =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(texto.take(19))!!.time

    private fun ProtocoloDto.aProtocolo() = Protocolo(
        id, numero, equipoId, tecnicoId, fechaALong(fechaHora), temperatura, humedad, estado,
        resultadoGlobal, motivo, comentarioIngeniero, formalizadoPorId, fechaFormalizacion?.let { fechaALong(it) }
    )

    fun protocolosDeEquipo(equipoId: Long, usuario: Usuario): Flow<List<Protocolo>> = flow {
        while (true) {
            val lista = try {
                Api.protocolos.deEquipo(equipoId, usuario.rol).map { it.aProtocolo() }
            } catch (e: Exception) {
                null
            }
            if (lista != null) emit(lista)
            delay(3000)
        }
    }.distinctUntilChanged()

    fun observarProtocolo(id: Long): Flow<Protocolo?> = flow {
        while (true) {
            val protocolo = try { Api.protocolos.protocolo(id).aProtocolo() } catch (e: Exception) { null }
            if (protocolo != null) emit(protocolo)
            delay(3000)
        }
    }.distinctUntilChanged()

    suspend fun obtenerProtocolo(id: Long): Protocolo? =
        try { Api.protocolos.protocolo(id).aProtocolo() } catch (e: Exception) { null }

    suspend fun contarProtocolos(): Int = protocoloDao.contar()

    suspend fun existeNumero(numero: String): Boolean = protocoloDao.contarConNumero(numero) > 0

    suspend fun crearProtocolo(protocolo: Protocolo): Long =
        Api.protocolos.crear(
            mapOf(
                "equipoId" to protocolo.equipoId,
                "tecnicoId" to protocolo.tecnicoId,
                "rol" to (Sesion.usuario?.rol ?: "TECNICO"),
                "temperatura" to protocolo.temperatura,
                "humedad" to protocolo.humedad
            )
        ).id

    suspend fun actualizarProtocolo(protocolo: Protocolo) {
        val accion = when (protocolo.estado) {
            "REGISTRADO_EN_TERRENO" -> "REGISTRAR_EN_TERRENO"
            "FORMALIZADO" -> "FORMALIZAR"
            "OBSERVADO" -> "OBSERVAR"
            "ANULADO" -> "ANULAR"
            else -> return
        }
        val usuario = Sesion.usuario ?: return
        try {
            Api.protocolos.cambiarEstado(
                protocolo.id,
                mapOf("accion" to accion, "usuarioId" to usuario.id, "rol" to usuario.rol, "texto" to protocolo.motivo)
            )
        } catch (e: Exception) {
        }
    }

    fun observarPruebas(protocoloId: Long): Flow<List<Prueba>> = flow {
        while (true) {
            val lista = try { Api.pruebas.deProtocolo(protocoloId) } catch (e: Exception) { null }
            if (lista != null) emit(lista)
            delay(3000)
        }
    }.distinctUntilChanged()

    suspend fun medicionesDe(pruebaId: Long): List<Medicion> =
        try { Api.pruebas.detalle(pruebaId).mediciones } catch (e: Exception) { emptyList() }

    suspend fun pruebaAnterior(equipoId: Long, tipoPrueba: String, fecha: Long): Prueba? =
        pruebaDao.pruebaAnterior(equipoId, tipoPrueba, fecha)

    suspend fun registrarPrueba(prueba: Prueba, mediciones: List<Medicion>) {
        bd.withTransaction {
            val pruebaId = pruebaDao.insertar(prueba)
            pruebaDao.insertarMediciones(mediciones.map { it.copy(pruebaId = pruebaId) })
            recalcularResultadoGlobal(prueba.protocoloId)
        }
    }

    private suspend fun recalcularResultadoGlobal(protocoloId: Long) {
        val protocolo = protocoloDao.obtener(protocoloId) ?: return
        val resultados = pruebaDao.deProtocolo(protocoloId)
            .filter { it.realizada }
            .mapNotNull { Resultado.desde(it.resultado) }
        protocoloDao.actualizar(protocolo.copy(resultadoGlobal = Resultado.peor(resultados)?.name))
    }

    companion object {
        @Volatile
        private var instancia: Repositorio? = null

        fun obtener(contexto: Context): Repositorio {
            return instancia ?: synchronized(this) {
                instancia ?: Repositorio(BaseDatos.obtener(contexto)).also { instancia = it }
            }
        }
    }
}