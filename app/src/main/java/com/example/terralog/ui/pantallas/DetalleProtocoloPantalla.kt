package com.example.terralog.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.R
import com.example.terralog.datos.Sesion
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.estadoEnum
import com.example.terralog.datos.resultadoEnum
import com.example.terralog.datos.resultadoGlobalEnum
import com.example.terralog.dominio.AccionProtocolo
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Formato
import com.example.terralog.dominio.Rol
import com.example.terralog.recursos.Vibracion
import com.example.terralog.ui.componentes.BarraSuperior
import com.example.terralog.ui.componentes.BotonPrincipal
import com.example.terralog.ui.componentes.BotonSecundario
import com.example.terralog.ui.componentes.CampoTexto
import com.example.terralog.ui.componentes.ChipEstado
import com.example.terralog.ui.componentes.ChipResultado
import com.example.terralog.ui.componentes.EncabezadoSeccion
import com.example.terralog.ui.componentes.FilaDato
import com.example.terralog.ui.componentes.TarjetaBase
import com.example.terralog.ui.theme.Dimens

@Composable
fun DetalleProtocoloPantalla(
    protocoloId: Long,
    alVolver: () -> Unit,
    vm: DetalleProtocoloViewModel = viewModel()
) {
    val contexto = LocalContext.current
    LaunchedEffect(protocoloId) { vm.cargar(protocoloId) }
    val protocolo = vm.protocolo
    val esCliente = Sesion.rol() == Rol.CLIENTE

    Scaffold(
        topBar = { BarraSuperior(stringResource(R.string.titulo_protocolo), alVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla)
        ) {
            if (protocolo != null) {
                val estado = protocolo.estadoEnum()

                TarjetaBase {
                    Text(protocolo.numero, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${vm.equipo?.equipo?.tag ?: ""} · ${vm.equipo?.instalacionNombre ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        ChipEstado(estado, paraCliente = esCliente)
                        ChipResultado(protocolo.resultadoGlobalEnum())
                    }
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    FilaDato("Fecha y hora", Formato.fechaHora(protocolo.fechaHora))
                    FilaDato("Técnico", vm.nombreTecnico)
                    FilaDato("Temperatura", "${Formato.numero(protocolo.temperatura)} °C")
                    FilaDato("Humedad relativa", "${Formato.numero(protocolo.humedad)} %")
                    if (estado == EstadoProtocolo.FORMALIZADO && vm.nombreFormalizador != null) {
                        FilaDato("Formalizado por", vm.nombreFormalizador ?: "")
                        protocolo.fechaFormalizacion?.let { FilaDato("Fecha de formalización", Formato.fechaHora(it)) }
                    }
                    if (!protocolo.motivo.isNullOrEmpty() && !esCliente) {
                        FilaDato("Motivo", protocolo.motivo)
                    }
                }

                AnimatedVisibility(visible = vm.mensaje != null, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = vm.mensaje ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = Dimens.espacioChico)
                    )
                }

                
                EncabezadoSeccion(stringResource(R.string.seccion_pruebas))
                if (vm.items.isEmpty()) {
                    Text(stringResource(R.string.sin_pruebas), style = MaterialTheme.typography.bodyMedium)
                }
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    vm.items.forEach { item -> TarjetaPrueba(item) }
                }

                val acciones = vm.accionesDisponibles()
                if (acciones.isNotEmpty()) {
                    EncabezadoSeccion(stringResource(R.string.seccion_acciones))
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        acciones.forEach { accion ->
                            val alHacerClic = { vm.ejecutar(accion, alError = { Vibracion.error(contexto) }) }
                            if (accion == AccionProtocolo.FORMALIZAR || accion == AccionProtocolo.REGISTRAR_EN_TERRENO) {
                                BotonPrincipal(texto = accion.texto, alHacerClic = alHacerClic)
                            } else {
                                BotonSecundario(texto = accion.texto, alHacerClic = alHacerClic)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.espacioGrande))
            }
        }
    }

    val accionDialogo = vm.dialogo
    if (accionDialogo != null) {
        val titulo = if (accionDialogo == AccionProtocolo.OBSERVAR) {
            stringResource(R.string.dialogo_observar)
        } else {
            stringResource(R.string.dialogo_anular)
        }
        AlertDialog(
            onDismissRequest = vm::cerrarDialogo,
            title = { Text(titulo) },
            text = {
                CampoTexto(
                    valor = vm.textoDialogo,
                    alCambiar = vm::cambiarTextoDialogo,
                    etiqueta = stringResource(R.string.campo_motivo),
                    icono = Icons.Default.Edit,
                    error = vm.errorDialogo,
                    unaLinea = false,
                    lineasMinimas = 2
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.confirmarDialogo(alError = { Vibracion.error(contexto) }) }) {
                    Text(stringResource(R.string.btn_confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = vm::cerrarDialogo) { Text(stringResource(R.string.btn_cancelar)) }
            }
        )
    }
}

@Composable
private fun TarjetaPrueba(item: ItemPrueba) {
    TarjetaBase {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.tipo.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (item.prueba.realizada) ChipResultado(item.prueba.resultadoEnum())
        }

        if (!item.prueba.realizada) {
            Text(
                text = "No realizada: ${item.prueba.motivoNoRealizada ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            if (item.instrumento != null) {
                Text("Instrumento: ${item.instrumento}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(Dimens.espacioChico))
            item.mediciones.forEach { FilaDato(it.nombre, textoMedicion(it)) }
        }
        if (!item.prueba.comentario.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(Dimens.espacioChico))
            Text("Comentario: ${item.prueba.comentario}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


private fun textoMedicion(m: Medicion): String {
    if (m.clave == "opera") return if (m.valor != 0.0) "Sí" else "No"
    return "${Formato.numero(m.valor)} ${m.unidad}".trim()
}