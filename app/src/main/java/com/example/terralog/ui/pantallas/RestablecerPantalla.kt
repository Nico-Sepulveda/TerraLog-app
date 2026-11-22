package com.example.terralog.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.recursos.Vibracion
import com.example.terralog.ui.componentes.BotonPrincipal
import com.example.terralog.ui.componentes.CampoTexto
import com.example.terralog.ui.theme.Dimens

@Composable
fun RestablecerPantalla(
    alVolver: () -> Unit,
    vm: RestablecerViewModel = viewModel()
) {
    val contexto = LocalContext.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(Dimens.espacioPantalla),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio, Alignment.CenterVertically)
        ) {
            Text(
                text = "Restablecer contraseña",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Escribe tu correo y la nueva contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            CampoTexto(
                valor = vm.correo,
                alCambiar = vm::cambiarCorreo,
                etiqueta = "Correo",
                icono = Icons.Default.Email,
                error = vm.errorCorreo,
                tipoTeclado = KeyboardType.Email
            )
            CampoTexto(
                valor = vm.clave,
                alCambiar = vm::cambiarClave,
                etiqueta = "Nueva contraseña",
                icono = Icons.Default.Lock,
                error = vm.errorClave,
                esClave = true
            )
            vm.mensaje?.let {
                Text(
                    text = it,
                    color = if (vm.exito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            BotonPrincipal(
                texto = "Cambiar contraseña",
                alHacerClic = { vm.restablecer(alError = { Vibracion.error(contexto) }) },
                habilitado = !vm.cargando
            )
            TextButton(onClick = alVolver) {
                Text("Volver al inicio de sesión")
            }
        }
    }
}