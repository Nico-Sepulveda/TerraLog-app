package com.example.terralog.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.terralog.R
import com.example.terralog.ui.estilos.EstilosCampo
import com.example.terralog.ui.theme.TerraLogTheme

@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false,
    unaLinea: Boolean = true,
    lineasMinimas: Int = 1,
    sufijo: String? = null
) {
    var verClave by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            if (esClave) {
                IconButton(onClick = { verClave = !verClave }) {
                    Icon(
                        imageVector = if (verClave) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = stringResource(
                            if (verClave) R.string.cd_ocultar_clave else R.string.cd_mostrar_clave
                        )
                    )
                }
            } else if (error != null) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(R.string.cd_error),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        suffix = if (sufijo != null && sufijo.isNotEmpty()) {
            { Text(sufijo) }
        } else {
            null
        },
        isError = error != null,
        
        supportingText = {
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(text = error ?: "")
            }
        },
        visualTransformation = if (esClave && !verClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else tipoTeclado),
        singleLine = unaLinea,
        minLines = lineasMinimas,
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores()
    )
}

@Preview(showBackground = true)
@Composable
fun CampoTextoPreview() {
    TerraLogTheme {
        CampoTexto(
            valor = "correo-malo",
            alCambiar = {},
            etiqueta = "Correo",
            icono = Icons.Default.Email,
            error = "Correo no valido"
        )
    }
}