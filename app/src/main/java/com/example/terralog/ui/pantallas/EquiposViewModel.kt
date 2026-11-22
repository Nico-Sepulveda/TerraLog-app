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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class EquiposViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)


    val equipos: StateFlow<List<EquipoConInstalacion>> = run {
        val usuario = Sesion.usuario
        if (usuario != null) repositorio.equiposParaUsuario(usuario) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var busqueda by mutableStateOf("")
        private set

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }


    fun filtrar(lista: List<EquipoConInstalacion>): List<EquipoConInstalacion> {
        val texto = busqueda.trim().lowercase()
        if (texto.isEmpty()) return lista
        return lista.filter {
            it.equipo.tag.lowercase().contains(texto) ||
                    it.equipo.nroSerie.lowercase().contains(texto) ||
                    it.instalacionNombre.lowercase().contains(texto)
        }
    }
}