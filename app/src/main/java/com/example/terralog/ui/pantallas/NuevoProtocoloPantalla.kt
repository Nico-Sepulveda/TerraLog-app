package com.example.terralog.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.R
import com.example.terralog.recursos.Vibracion
import com.example.terralog.ui.componentes.BarraSuperior
import com.example.terralog.ui.componentes.BotonPrincipal
import com.example.terralog.ui.componentes.CampoTexto
import com.example.terralog.ui.componentes.ConfirmacionGuardado
import com.example.terralog.ui.componentes.FilaDato
import com.example.terralog.ui.componentes.TarjetaBase
import com.example.terralog.ui.theme.Dimens
import kotlinx.coroutines.delay


@Composable
fun NuevoProtocoloPantalla(
    equipoId: Long,
    alVolver: () -> Unit,
    alCrear: (Long) -> Unit,
    vm: NuevoProtocoloViewModel = viewModel()
) {
    val contexto = LocalContext.current
    LaunchedEffect(equipoId) { vm.cargar(equipoId) }


    LaunchedEffect(vm.protocoloCreadoId) {
        val id = vm.protocoloCreadoId
        if (id != null) {
            Vibracion.exito(contexto)
            delay(900)
            alCrear(id)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { BarraSuperior(stringResource(R.string.titulo_nuevo_protocolo), alVolver) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(Dimens.espacioPantalla)
            ) {
                val datos = vm.equipo
                if (datos != null) {
                    TarjetaBase {
                        Text(datos.equipo.tag, style = MaterialTheme.typography.titleMedium)
                        FilaDato("Instalación", datos.instalacionNombre)
                        FilaDato("Cliente", datos.clienteNombre)
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.espacioMedio))
                Text(
                    text = stringResource(R.string.indicacion_ambiente),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(Dimens.espacioMedio))
                CampoTexto(
                    valor = vm.temperatura,
                    alCambiar = vm::cambiarTemperatura,
                    etiqueta = stringResource(R.string.campo_temperatura),
                    icono = Icons.Default.Info,
                    error = vm.errorTemperatura,
                    tipoTeclado = KeyboardType.Decimal,
                    sufijo = "°C"
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                CampoTexto(
                    valor = vm.humedad,
                    alCambiar = vm::cambiarHumedad,
                    etiqueta = stringResource(R.string.campo_humedad),
                    icono = Icons.Default.Settings,
                    error = vm.errorHumedad,
                    tipoTeclado = KeyboardType.Decimal,
                    sufijo = "%"
                )
                Spacer(modifier = Modifier.height(Dimens.espacioGrande))
                BotonPrincipal(
                    texto = stringResource(R.string.btn_crear_protocolo),
                    alHacerClic = { vm.crear(equipoId, alError = { Vibracion.error(contexto) }) }
                )
            }
        }
        ConfirmacionGuardado(visible = vm.protocoloCreadoId != null, mensaje = stringResource(R.string.protocolo_creado))
    }
}