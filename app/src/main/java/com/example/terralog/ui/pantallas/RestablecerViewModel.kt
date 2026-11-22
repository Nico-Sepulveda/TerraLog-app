package com.example.terralog.ui.pantallas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terralog.datos.Repositorio
import com.example.terralog.dominio.Validaciones
import kotlinx.coroutines.launch

class RestablecerViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)

    var correo by mutableStateOf("")
        private set
    var clave by mutableStateOf("")
        private set
    var errorCorreo by mutableStateOf<String?>(null)
        private set
    var errorClave by mutableStateOf<String?>(null)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set
    var exito by mutableStateOf(false)
        private set
    var cargando by mutableStateOf(false)
        private set

    fun cambiarCorreo(texto: String) {
        correo = texto
        errorCorreo = Validaciones.validarCorreo(texto)
        mensaje = null
    }

    fun cambiarClave(texto: String) {
        clave = texto
        errorClave = Validaciones.validarClave(texto)
        mensaje = null
    }

    fun restablecer(alError: () -> Unit) {
        errorCorreo = Validaciones.validarCorreo(correo)
        errorClave = Validaciones.validarClave(clave)
        if (errorCorreo != null || errorClave != null) {
            alError()
            return
        }
        viewModelScope.launch {
            cargando = true
            exito = repositorio.restablecerClave(correo.trim(), clave)
            cargando = false
            mensaje = if (exito) "Contraseña actualizada. Ya puedes ingresar." else "No se pudo cambiar: revisa el correo (la clave nueva debe tener 6 o más caracteres)."
            if (!exito) alError()
        }
    }
}