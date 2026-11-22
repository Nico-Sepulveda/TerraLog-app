package com.example.terralog.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.R
import com.example.terralog.datos.Sesion
import com.example.terralog.datos.entidades.EquipoConInstalacion
import com.example.terralog.dominio.Permisos
import com.example.terralog.dominio.TipoEquipo
import com.example.terralog.ui.componentes.BarraSuperior
import com.example.terralog.ui.componentes.CampoTexto
import com.example.terralog.ui.componentes.MensajeVacio
import com.example.terralog.ui.componentes.TarjetaBase
import com.example.terralog.ui.theme.Blanco
import com.example.terralog.ui.theme.Dimens
import com.example.terralog.ui.theme.Primario

@Composable
fun ListaEquiposPantalla(
    alAgregar: () -> Unit,
    alVerEquipo: (Long) -> Unit,
    alCerrarSesion: () -> Unit,
    vm: EquiposViewModel = viewModel()
) {
    val equipos by vm.equipos.collectAsState()
    val filtrados = vm.filtrar(equipos)
    val usuario = Sesion.usuario

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.titulo_equipos),
                acciones = {
                    IconButton(onClick = alCerrarSesion) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.cd_cerrar_sesion)
                        )
                    }
                }
            )
        },
        floatingActionButton = {

            if (Permisos.puedeRegistrar(Sesion.rol())) {
                FloatingActionButton(onClick = alAgregar, containerColor = Primario, contentColor = Blanco) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_agregar_equipo))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimens.espacioPantalla)
        ) {
            if (usuario != null) {
                Text(
                    text = "${usuario.nombre} · ${Sesion.rol().nombre}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = Dimens.espacioMedio)
                )
            }
            CampoTexto(
                valor = vm.busqueda,
                alCambiar = vm::cambiarBusqueda,
                etiqueta = stringResource(R.string.buscar_equipo),
                icono = Icons.Default.Search
            )
            Spacer(modifier = Modifier.height(Dimens.espacioChico))

            if (filtrados.isEmpty()) {
                MensajeVacio(stringResource(R.string.sin_equipos))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(filtrados, key = { it.equipo.id }) { item ->
                        TarjetaEquipo(item = item, alHacerClic = { alVerEquipo(item.equipo.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEquipo(item: EquipoConInstalacion, alHacerClic: () -> Unit) {
    val tipo = TipoEquipo.desde(item.equipo.tipo)
    TarjetaBase(alHacerClic = alHacerClic) {
        Text(item.equipo.tag, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "${tipo?.nombre ?: item.equipo.tipo} · ${tipo?.clase?.nombre ?: ""}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${item.instalacionNombre} — ${item.clienteNombre}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}