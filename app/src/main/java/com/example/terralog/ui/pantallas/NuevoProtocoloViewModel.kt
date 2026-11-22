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
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Validaciones
import kotlinx.coroutines.launch
import java.util.Calendar

class NuevoProtocoloViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)

    var equipo by mutableStateOf<EquipoConInstalacion?>(null)
        private set
    var temperatura by mutableStateOf("")
        private set
    var humedad by mutableStateOf("")
        private set
    var errorTemperatura by mutableStateOf<String?>(null)
        private set
    var errorHumedad by mutableStateOf<String?>(null)
        private set


    var protocoloCreadoId by mutableStateOf<Long?>(null)
        private set

    fun cargar(equipoId: Long) {
        if (equipo != null) return
        viewModelScope.launch { equipo = repositorio.obtenerEquipo(equipoId) }
    }

    fun cambiarTemperatura(texto: String) {
        temperatura = texto
        errorTemperatura = Validaciones.validarTemperatura(texto)
    }

    fun cambiarHumedad(texto: String) {
        humedad = texto
        errorHumedad = Validaciones.validarHumedad(texto)
    }


    fun crear(equipoId: Long, alError: () -> Unit) {
        errorTemperatura = Validaciones.validarTemperatura(temperatura)
        errorHumedad = Validaciones.validarHumedad(humedad)
        if (errorTemperatura != null || errorHumedad != null) {
            alError()
            return
        }
        val usuario = Sesion.usuario ?: return
        viewModelScope.launch {
            val anio = Calendar.getInstance().get(Calendar.YEAR)
            
            var correlativo = repositorio.contarProtocolos() + 1
            var numero = formatoNumero(anio, correlativo)
            while (repositorio.existeNumero(numero)) {
                correlativo++
                numero = formatoNumero(anio, correlativo)
            }
            protocoloCreadoId = repositorio.crearProtocolo(
                Protocolo(
                    numero = numero,
                    equipoId = equipoId,
                    tecnicoId = usuario.id,
                    fechaHora = System.currentTimeMillis(),
                    temperatura = Validaciones.aNumero(temperatura) ?: 0.0,
                    humedad = Validaciones.aNumero(humedad) ?: 0.0,
                    estado = EstadoProtocolo.BORRADOR.name
                )
            )
        }
    }

    private fun formatoNumero(anio: Int, correlativo: Int): String =
        "PT-$anio-${correlativo.toString().padStart(4, '0')}"
}