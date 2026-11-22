package com.example.terralog.ui.pantallas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terralog.R
import com.example.terralog.datos.Repositorio
import com.example.terralog.datos.Sesion
import com.example.terralog.dominio.Validaciones
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)

    var correo by mutableStateOf("")
        private set
    var clave by mutableStateOf("")
        private set
    var errorCorreo by mutableStateOf<String?>(null)
        private set
    var errorClave by mutableStateOf<String?>(null)
        private set
    var errorGeneral by mutableStateOf<String?>(null)
        private set
    var cargando by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch { repositorio.sembrarDatosSiVacio() }
    }


    fun cambiarCorreo(texto: String) {
        correo = texto
        errorCorreo = Validaciones.validarCorreo(texto)
        errorGeneral = null
    }

    fun cambiarClave(texto: String) {
        clave = texto
        errorClave = Validaciones.validarClave(texto)
        errorGeneral = null
    }

    fun ingresar(alExito: () -> Unit, alError: () -> Unit) {
        errorCorreo = Validaciones.validarCorreo(correo)
        errorClave = Validaciones.validarClave(clave)
        if (errorCorreo != null || errorClave != null) {
            alError()
            return
        }
        viewModelScope.launch {
            cargando = true
            val usuario = repositorio.iniciarSesion(correo.trim(), clave)
            cargando = false
            if (usuario == null) {
                errorGeneral = getApplication<Application>().getString(R.string.login_error_credenciales)
                alError()
            } else {
                Sesion.iniciar(usuario)
                alExito()
            }
        }
    }
}
