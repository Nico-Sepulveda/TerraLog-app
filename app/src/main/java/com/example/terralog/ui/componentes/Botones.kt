package com.example.terralog.ui.componentes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.terralog.ui.estilos.EstilosBotones
import com.example.terralog.ui.estilos.estiloAltoBoton
import com.example.terralog.ui.theme.Dimens
import com.example.terralog.ui.theme.TerraLogTheme

@Composable
fun BotonPrincipal(
    texto: String,
    alHacerClic: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    habilitado: Boolean = true
) {
    Button(
        onClick = alHacerClic,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().estiloAltoBoton(),
        shape = EstilosBotones.forma,
        colors = EstilosBotones.colorPrincipal(),
        elevation = EstilosBotones.elevacion(),
        contentPadding = EstilosBotones.relleno
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(Dimens.tamanoIcono))
            Spacer(modifier = Modifier.width(Dimens.espacioChico))
        }
        Text(texto)
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    alHacerClic: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    habilitado: Boolean = true
) {
    OutlinedButton(
        onClick = alHacerClic,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().estiloAltoBoton(),
        shape = EstilosBotones.forma,
        colors = EstilosBotones.coloresSecundario(),
        border = EstilosBotones.bordeSecundario(),
        contentPadding = EstilosBotones.relleno
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(Dimens.tamanoIcono))
            Spacer(modifier = Modifier.width(Dimens.espacioChico))
        }
        Text(texto)
    }
}

@Preview(showBackground = true)
@Composable
fun BotonesPreview() {
    TerraLogTheme {
        BotonPrincipal(texto = "Guardar", alHacerClic = {}, icono = Icons.Default.Save)
    }
}
