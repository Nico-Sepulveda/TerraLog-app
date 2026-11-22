package com.example.terralog.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.R
import com.example.terralog.datos.Sesion
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.datos.estadoEnum
import com.example.terralog.datos.resultadoGlobalEnum
import com.example.terralog.dominio.Formato
import com.example.terralog.dominio.Permisos
import com.example.terralog.dominio.Rol
import com.example.terralog.dominio.TipoEquipo
import com.example.terralog.recursos.Camara
import com.example.terralog.ui.componentes.BarraSuperior
import com.example.terralog.ui.componentes.BotonPrincipal
import com.example.terralog.ui.componentes.ChipEstado
import com.example.terralog.ui.componentes.ChipResultado
import com.example.terralog.ui.componentes.EncabezadoSeccion
import com.example.terralog.ui.componentes.FilaDato
import com.example.terralog.ui.componentes.MensajeVacio
import com.example.terralog.ui.componentes.TarjetaBase
import com.example.terralog.ui.theme.Dimens

@Composable
fun DetalleEquipoPantalla(
    equipoId: Long,
    alVolver: () -> Unit,
    alIniciarProtocolo: (Long) -> Unit,
    alVerProtocolo: (Long) -> Unit,
    vm: DetalleEquipoViewModel = viewModel()
) {
    LaunchedEffect(equipoId) { vm.cargar(equipoId) }
    val datos = vm.equipo
    val esCliente = Sesion.rol() == Rol.CLIENTE

    Scaffold(
        topBar = { BarraSuperior(stringResource(R.string.titulo_detalle_equipo), alVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla)
        ) {
            if (vm.sinAcceso) {
                MensajeVacio(stringResource(R.string.sin_acceso))
            } else if (datos != null) {
                val equipo = datos.equipo
                val tipo = TipoEquipo.desde(equipo.tipo)

                TarjetaBase {
                    Text(equipo.tag, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    FilaDato("Tipo", "${tipo?.nombre ?: equipo.tipo} (${tipo?.clase?.nombre ?: ""})")
                    FilaDato("Instalación", datos.instalacionNombre)
                    FilaDato("Cliente", datos.clienteNombre)
                    FilaDato("Marca", equipo.marca)
                    FilaDato("N° de serie", equipo.nroSerie)
                    FilaDato("Año", equipo.anio.toString())
                    FilaDato("Datos de placa", equipo.datosPlaca)
                    if (equipo.maxContactos != null) {
                        FilaDato("Máx. contactos", "${Formato.numero(equipo.maxContactos)} µΩ")
                    }
                }

                
                val rutaFoto = equipo.rutaFotoPlaca
                if (rutaFoto != null) {
                    val imagen = remember(rutaFoto) { Camara.cargarFoto(rutaFoto) }
                    if (imagen != null) {
                        EncabezadoSeccion(stringResource(R.string.seccion_foto))
                        Image(
                            bitmap = imagen,
                            contentDescription = stringResource(R.string.cd_foto_placa),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.altoFoto)
                                .clip(RoundedCornerShape(Dimens.radioCampo))
                        )
                    }
                }

                
                if (Permisos.puedeRegistrar(Sesion.rol())) {
                    Spacer(modifier = Modifier.height(Dimens.espacioMedio))
                    BotonPrincipal(
                        texto = stringResource(R.string.btn_nuevo_protocolo),
                        alHacerClic = { alIniciarProtocolo(equipo.id) },
                        icono = Icons.Default.Add
                    )
                }

                
                EncabezadoSeccion(stringResource(R.string.historial_protocolos))
                if (vm.protocolos.isEmpty()) {
                    MensajeVacio(stringResource(R.string.sin_protocolos))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        vm.protocolos.forEach { protocolo ->
                            TarjetaProtocolo(protocolo, esCliente) { alVerProtocolo(protocolo.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaProtocolo(protocolo: Protocolo, esCliente: Boolean, alHacerClic: () -> Unit) {
    TarjetaBase(alHacerClic = alHacerClic) {
        Text(protocolo.numero, style = MaterialTheme.typography.titleMedium)
        Text(Formato.fechaHora(protocolo.fechaHora), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(Dimens.espacioChico))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            ChipEstado(protocolo.estadoEnum(), paraCliente = esCliente)
            ChipResultado(protocolo.resultadoGlobalEnum())
        }
    }
}