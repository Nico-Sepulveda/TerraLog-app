package com.example.terralog.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terralog.R
import com.example.terralog.recursos.Vibracion
import com.example.terralog.ui.componentes.BotonPrincipal
import com.example.terralog.ui.componentes.CampoTexto
import com.example.terralog.ui.theme.Blanco
import com.example.terralog.ui.theme.Dimens
import com.example.terralog.ui.theme.Primario

@Composable
fun LoginPantalla(
    alIngresar: () -> Unit,
    vm: LoginViewModel = viewModel()
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
            Box(
                modifier = Modifier.size(Dimens.tamanoLogo).clip(CircleShape).background(Primario),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logo_login),
                    contentDescription = null,
                    tint = Blanco,
                    modifier = Modifier.size(Dimens.tamanoIconoGrande / 1.5f)
                )
            }
            Text(
                text = stringResource(R.string.login_titulo),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.login_subtitulo),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.espacioChico))

            CampoTexto(
                valor = vm.correo,
                alCambiar = vm::cambiarCorreo,
                etiqueta = stringResource(R.string.campo_correo),
                icono = Icons.Default.Email,
                error = vm.errorCorreo,
                tipoTeclado = KeyboardType.Email
            )
            CampoTexto(
                valor = vm.clave,
                alCambiar = vm::cambiarClave,
                etiqueta = stringResource(R.string.campo_clave),
                icono = Icons.Default.Lock,
                error = vm.errorClave,
                esClave = true
            )


            AnimatedVisibility(
                visible = vm.errorGeneral != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = vm.errorGeneral ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            BotonPrincipal(
                texto = stringResource(R.string.btn_ingresar),
                alHacerClic = {
                    vm.ingresar(alExito = alIngresar, alError = { Vibracion.error(contexto) })
                },
                habilitado = !vm.cargando
            )
        }
    }
}