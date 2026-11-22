package com.example.terralog.ui.pantallas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terralog.datos.Repositorio
import com.example.terralog.datos.Sesion
import com.example.terralog.datos.entidades.EquipoConInstalacion
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.datos.entidades.Prueba
import com.example.terralog.datos.estadoEnum
import com.example.terralog.datos.tipoEnum
import com.example.terralog.dominio.AccionProtocolo
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Permisos
import com.example.terralog.dominio.TipoPrueba
import com.example.terralog.dominio.Validaciones
import kotlinx.coroutines.launch

data class ItemPrueba(
    val prueba: Prueba,
    val tipo: TipoPrueba,
    val mediciones: List<Medicion>,
    val instrumento: String?
)

class DetalleProtocoloViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)

    var protocolo by mutableStateOf<Protocolo?>(null)
        private set
    var equipo by mutableStateOf<EquipoConInstalacion?>(null)
        private set
    var nombreTecnico by mutableStateOf("")
        private set
    var nombreFormalizador by mutableStateOf<String?>(null)
        private set
    var items by mutableStateOf(listOf<ItemPrueba>())
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set


    var dialogo by mutableStateOf<AccionProtocolo?>(null)
        private set
    var textoDialogo by mutableStateOf("")
        private set
    var errorDialogo by mutableStateOf<String?>(null)
        private set

    private var cargadoId: Long? = null


    fun accionesDisponibles(): List<AccionProtocolo> {
        val p = protocolo ?: return emptyList()
        return Permisos.acciones(Sesion.rol(), p.estadoEnum())
    }

    fun cargar(protocoloId: Long) {
        if (cargadoId == protocoloId) return
        cargadoId = protocoloId


        viewModelScope.launch {
            repositorio.observarProtocolo(protocoloId).collect { p ->
                protocolo = p
                if (p != null) {
                    equipo = repositorio.obtenerEquipo(p.equipoId)
                    nombreTecnico = repositorio.obtenerUsuario(p.tecnicoId)?.nombre ?: ""
                    nombreFormalizador = p.formalizadoPorId?.let { repositorio.obtenerUsuario(it)?.nombre }
                }
            }
        }
        viewModelScope.launch {
            repositorio.observarPruebas(protocoloId).collect { lista ->
                items = lista.mapNotNull { construirItem(it) }
            }
        }
    }

    private suspend fun construirItem(prueba: Prueba): ItemPrueba? {
        val tipo = prueba.tipoEnum() ?: return null
        val mediciones = repositorio.medicionesDe(prueba.id)
        val instrumento = prueba.instrumentoId?.let { id ->
            repositorio.instrumentos().firstOrNull { it.id == id }?.nombre
        }
        return ItemPrueba(prueba, tipo, mediciones, instrumento)
    }


    fun ejecutar(accion: AccionProtocolo, alError: () -> Unit) {
        val p = protocolo ?: return
        mensaje = null
        when (accion) {
            AccionProtocolo.REGISTRAR_EN_TERRENO -> {
                
                if (items.none { it.prueba.realizada }) {
                    mensaje = "Registra al menos una prueba realizada antes de pasar a Registrado en terreno"
                    alError()
                } else {
                    actualizar(p.copy(estado = EstadoProtocolo.REGISTRADO_EN_TERRENO.name))
                }
            }
            AccionProtocolo.FORMALIZAR -> {
                actualizar(
                    p.copy(
                        estado = EstadoProtocolo.FORMALIZADO.name,
                        formalizadoPorId = Sesion.usuario?.id,
                        fechaFormalizacion = System.currentTimeMillis()
                    )
                )
            }
            AccionProtocolo.OBSERVAR, AccionProtocolo.ANULAR -> abrirDialogo(accion)
        }
    }

    private fun actualizar(nuevo: Protocolo) {
        viewModelScope.launch { repositorio.actualizarProtocolo(nuevo) }
    }


    private fun abrirDialogo(accion: AccionProtocolo) {
        dialogo = accion
        textoDialogo = ""
        errorDialogo = null
    }

    fun cambiarTextoDialogo(texto: String) {
        textoDialogo = texto
        errorDialogo = Validaciones.validarMotivo(texto)
    }

    fun cerrarDialogo() {
        dialogo = null
    }

    fun confirmarDialogo(alError: () -> Unit) {
        val p = protocolo ?: return
        val accion = dialogo ?: return
        errorDialogo = Validaciones.validarMotivo(textoDialogo)
        if (errorDialogo != null) {
            alError()
            return
        }
        val texto = textoDialogo.trim()
        when (accion) {
            AccionProtocolo.OBSERVAR -> actualizar(p.copy(estado = EstadoProtocolo.OBSERVADO.name, motivo = texto))
            AccionProtocolo.ANULAR -> actualizar(p.copy(estado = EstadoProtocolo.ANULADO.name, motivo = texto))
            else -> Unit
        }
        dialogo = null
    }
}