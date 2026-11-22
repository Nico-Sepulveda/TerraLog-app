package com.example.terralog.ui.pantallas

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terralog.R
import com.example.terralog.datos.Repositorio
import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.InstalacionConCliente
import com.example.terralog.dominio.TipoEquipo
import com.example.terralog.dominio.Validaciones
import com.example.terralog.recursos.Camara
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegistrarEquipoViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = Repositorio.obtener(app)

    val instalaciones: StateFlow<List<InstalacionConCliente>> = repositorio.instalaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    var tag by mutableStateOf("")
        private set
    var tipo by mutableStateOf<TipoEquipo?>(null)
        private set
    var instalacionId by mutableStateOf<Long?>(null)
        private set
    var marca by mutableStateOf("")
        private set
    var nroSerie by mutableStateOf("")
        private set
    var anio by mutableStateOf("")
        private set
    var datosPlaca by mutableStateOf("")
        private set
    var maxContactos by mutableStateOf("")
        private set


    var errorTag by mutableStateOf<String?>(null)
        private set
    var errorTipo by mutableStateOf<String?>(null)
        private set
    var errorInstalacion by mutableStateOf<String?>(null)
        private set
    var errorMarca by mutableStateOf<String?>(null)
        private set
    var errorNroSerie by mutableStateOf<String?>(null)
        private set
    var errorAnio by mutableStateOf<String?>(null)
        private set
    var errorDatosPlaca by mutableStateOf<String?>(null)
        private set
    var errorMaxContactos by mutableStateOf<String?>(null)
        private set


    var foto by mutableStateOf<Bitmap?>(null)
        private set
    var mensajeCamara by mutableStateOf<String?>(null)
        private set

    var guardado by mutableStateOf(false)
        private set


    private var tagsExistentes: List<String> = emptyList()

    fun cambiarTag(texto: String) {
        tag = texto
        errorTag = Validaciones.validarTag(texto, tagsExistentes)
    }

    fun elegirTipo(nuevo: TipoEquipo) {
        tipo = nuevo
        errorTipo = null
        if (!nuevo.usaContactos) errorMaxContactos = null
    }

    fun elegirInstalacion(id: Long) {
        instalacionId = id
        errorInstalacion = null
        viewModelScope.launch {
            tagsExistentes = repositorio.tagsDeInstalacion(id)
            if (tag.isNotEmpty()) errorTag = Validaciones.validarTag(tag, tagsExistentes)
        }
    }

    fun cambiarMarca(texto: String) {
        marca = texto
        errorMarca = Validaciones.validarTextoObligatorio(texto, "la marca")
    }

    fun cambiarNroSerie(texto: String) {
        nroSerie = texto
        errorNroSerie = Validaciones.validarTextoObligatorio(texto, "el número de serie", 3)
    }

    fun cambiarAnio(texto: String) {
        anio = texto
        errorAnio = Validaciones.validarAnio(texto)
    }

    fun cambiarDatosPlaca(texto: String) {
        datosPlaca = texto
        errorDatosPlaca = Validaciones.validarTextoObligatorio(texto, "los datos de placa", 3)
    }

    fun cambiarMaxContactos(texto: String) {
        maxContactos = texto
        errorMaxContactos = Validaciones.validarMaxContactos(texto)
    }

    fun recibirFoto(nueva: Bitmap) {
        foto = nueva
        mensajeCamara = null
    }


    fun permisoCamaraDenegado() {
        mensajeCamara = getApplication<Application>().getString(R.string.foto_sin_permiso)
    }

    fun guardar(alError: () -> Unit) {
        val tipoElegido = tipo
        errorTag = Validaciones.validarTag(tag, tagsExistentes)
        errorTipo = Validaciones.validarTipoEquipo(tipoElegido)
        errorInstalacion = Validaciones.validarInstalacion(instalacionId)
        errorMarca = Validaciones.validarTextoObligatorio(marca, "la marca")
        errorNroSerie = Validaciones.validarTextoObligatorio(nroSerie, "el número de serie", 3)
        errorAnio = Validaciones.validarAnio(anio)
        errorDatosPlaca = Validaciones.validarTextoObligatorio(datosPlaca, "los datos de placa", 3)
        errorMaxContactos = if (tipoElegido != null && tipoElegido.usaContactos) {
            Validaciones.validarMaxContactos(maxContactos)
        } else {
            null
        }

        val hayErrores = listOf(
            errorTag, errorTipo, errorInstalacion, errorMarca,
            errorNroSerie, errorAnio, errorDatosPlaca, errorMaxContactos
        ).any { it != null }
        if (hayErrores || tipoElegido == null || instalacionId == null) {
            alError()
            return
        }

        viewModelScope.launch {
            val ruta = foto?.let { Camara.guardarFoto(getApplication(), it) }
            repositorio.registrarEquipo(
                Equipo(
                    instalacionId = instalacionId!!,
                    tag = tag.trim(),
                    tipo = tipoElegido.name,
                    marca = marca.trim(),
                    nroSerie = nroSerie.trim(),
                    anio = anio.trim().toInt(),
                    datosPlaca = datosPlaca.trim(),
                    maxContactos = if (tipoElegido.usaContactos) Validaciones.aNumero(maxContactos) else null,
                    rutaFotoPlaca = ruta
                )
            )
            guardado = true
        }
    }
}