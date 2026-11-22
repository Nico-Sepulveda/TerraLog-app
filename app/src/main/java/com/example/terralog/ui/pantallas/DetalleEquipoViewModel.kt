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
import com.example.terralog.dominio.Rol
import kotlinx.coroutines.launch

class DetalleEquipoViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)

    var equipo by mutableStateOf<EquipoConInstalacion?>(null)
        private set
    var protocolos by mutableStateOf(listOf<Protocolo>())
        private set
    var sinAcceso by mutableStateOf(false)
        private set

    private var cargadoId: Long? = null

    fun cargar(equipoId: Long) {
        if (cargadoId == equipoId) return
        cargadoId = equipoId
        val usuario = Sesion.usuario ?: return
        viewModelScope.launch {
            val encontrado = repositorio.obtenerEquipo(equipoId)

            if (encontrado != null && Rol.desde(usuario.rol) == Rol.CLIENTE && encontrado.clienteId != usuario.clienteId) {
                sinAcceso = true
                return@launch
            }
            equipo = encontrado

            repositorio.protocolosDeEquipo(equipoId, usuario).collect { protocolos = it }
        }
    }
}