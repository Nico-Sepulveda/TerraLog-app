package com.example.terralog.ui.pantallas

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.R
import com.example.terralog.dominio.TipoEquipo
import com.example.terralog.recursos.Vibracion
import com.example.terralog.ui.componentes.BarraSuperior
import com.example.terralog.ui.componentes.BotonPrincipal
import com.example.terralog.ui.componentes.BotonSecundario
import com.example.terralog.ui.componentes.CampoTexto
import com.example.terralog.ui.componentes.ConfirmacionGuardado
import com.example.terralog.ui.componentes.EncabezadoSeccion
import com.example.terralog.ui.componentes.ErrorAnimado
import com.example.terralog.ui.componentes.OpcionRadio
import com.example.terralog.ui.theme.Dimens
import kotlinx.coroutines.delay

@Composable
fun RegistrarEquipoPantalla(
    alVolver: () -> Unit,
    vm: RegistrarEquipoViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val instalaciones by vm.instalaciones.collectAsState()


    
    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto ->
        if (foto != null) vm.recibirFoto(foto)
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) lanzadorCamara.launch(null) else vm.permisoCamaraDenegado()
    }

    fun tomarFoto() {
        val tienePermiso = ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        if (tienePermiso) lanzadorCamara.launch(null) else lanzadorPermiso.launch(Manifest.permission.CAMERA)
    }


    LaunchedEffect(vm.guardado) {
        if (vm.guardado) {
            Vibracion.exito(contexto)
            delay(1000)
            alVolver()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { BarraSuperior(stringResource(R.string.titulo_registrar_equipo), alVolver) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(Dimens.espacioPantalla)
            ) {
                CampoTexto(
                    valor = vm.tag,
                    alCambiar = vm::cambiarTag,
                    etiqueta = stringResource(R.string.campo_tag),
                    icono = Icons.Default.Bolt,
                    error = vm.errorTag
                )

                EncabezadoSeccion(stringResource(R.string.etiqueta_tipo))
                TipoEquipo.entries.forEach { tipo ->
                    OpcionRadio(
                        texto = tipo.nombre,
                        detalle = tipo.clase.nombre,
                        seleccionado = vm.tipo == tipo,
                        alSeleccionar = { vm.elegirTipo(tipo) }
                    )
                }
                ErrorAnimado(vm.errorTipo)

                EncabezadoSeccion(stringResource(R.string.etiqueta_instalacion))
                instalaciones.forEach { item ->
                    OpcionRadio(
                        texto = item.instalacion.nombre,
                        detalle = item.clienteNombre,
                        seleccionado = vm.instalacionId == item.instalacion.id,
                        alSeleccionar = { vm.elegirInstalacion(item.instalacion.id) }
                    )
                }
                ErrorAnimado(vm.errorInstalacion)

                EncabezadoSeccion(stringResource(R.string.seccion_datos_placa))
                CampoTexto(
                    valor = vm.marca,
                    alCambiar = vm::cambiarMarca,
                    etiqueta = stringResource(R.string.campo_marca),
                    icono = Icons.Default.Build,
                    error = vm.errorMarca
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                CampoTexto(
                    valor = vm.nroSerie,
                    alCambiar = vm::cambiarNroSerie,
                    etiqueta = stringResource(R.string.campo_nro_serie),
                    icono = Icons.Default.Info,
                    error = vm.errorNroSerie
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                CampoTexto(
                    valor = vm.anio,
                    alCambiar = vm::cambiarAnio,
                    etiqueta = stringResource(R.string.campo_anio),
                    icono = Icons.Default.DateRange,
                    error = vm.errorAnio,
                    tipoTeclado = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                CampoTexto(
                    valor = vm.datosPlaca,
                    alCambiar = vm::cambiarDatosPlaca,
                    etiqueta = stringResource(R.string.campo_datos_placa),
                    icono = Icons.Default.Edit,
                    error = vm.errorDatosPlaca
                )
                
                AnimatedVisibility(
                    visible = vm.tipo?.usaContactos == true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(Dimens.espacioChico))
                        CampoTexto(
                            valor = vm.maxContactos,
                            alCambiar = vm::cambiarMaxContactos,
                            etiqueta = stringResource(R.string.campo_max_contactos),
                            icono = Icons.Default.Settings,
                            error = vm.errorMaxContactos,
                            tipoTeclado = KeyboardType.Decimal,
                            sufijo = "µΩ"
                        )
                    }
                }

                EncabezadoSeccion(stringResource(R.string.seccion_foto))
                val foto = vm.foto
                if (foto != null) {
                    Image(
                        bitmap = foto.asImageBitmap(),
                        contentDescription = stringResource(R.string.cd_foto_placa),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.altoFoto)
                            .clip(RoundedCornerShape(Dimens.radioCampo))
                    )
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                }
                BotonSecundario(
                    texto = stringResource(if (foto == null) R.string.btn_tomar_foto else R.string.btn_repetir_foto),
                    alHacerClic = { tomarFoto() },
                    icono = Icons.Default.CameraAlt
                )

                AnimatedVisibility(visible = vm.mensajeCamara != null, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = vm.mensajeCamara ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = Dimens.espacioChico)
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.espacioGrande))
                BotonPrincipal(
                    texto = stringResource(R.string.btn_guardar_equipo),
                    alHacerClic = { vm.guardar(alError = { Vibracion.error(contexto) }) }
                )
                Spacer(modifier = Modifier.height(Dimens.espacioGrande))
            }
        }
        ConfirmacionGuardado(visible = vm.guardado, mensaje = stringResource(R.string.equipo_guardado))
    }
}