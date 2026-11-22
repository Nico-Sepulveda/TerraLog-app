package com.example.terralog.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.terralog.ui.estilos.EstilosTarjeta
import com.example.terralog.ui.theme.Dimens
import com.example.terralog.ui.theme.Exito
import com.example.terralog.ui.theme.GrisDeshabilitado
import com.example.terralog.ui.theme.TerraLogTheme


@Composable
fun TarjetaBase(
    modifier: Modifier = Modifier,
    alHacerClic: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    if (alHacerClic != null) {
        Card(
            onClick = alHacerClic,
            modifier = modifier.fillMaxWidth().animateContentSize(),
            shape = EstilosTarjeta.forma,
            colors = EstilosTarjeta.colores(),
            elevation = EstilosTarjeta.elevacion()
        ) {
            Column(modifier = Modifier.padding(Dimens.espacioMedio), content = contenido)
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth().animateContentSize(),
            shape = EstilosTarjeta.forma,
            colors = EstilosTarjeta.colores(),
            elevation = EstilosTarjeta.elevacion()
        ) {
            Column(modifier = Modifier.padding(Dimens.espacioMedio), content = contenido)
        }
    }
}

@Composable
fun EncabezadoSeccion(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = Dimens.espacioMedio, bottom = Dimens.espacioChico)
    )
}


@Composable
fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.espacioMini),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(0.4f)
        )
        Spacer(modifier = Modifier.width(Dimens.espacioChico))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun MensajeVacio(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(Dimens.espacioGrande)
    )
}


@Composable
fun OpcionRadio(
    texto: String,
    seleccionado: Boolean,
    alSeleccionar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    detalle: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = seleccionado,
                enabled = habilitado,
                role = Role.RadioButton,
                onClick = alSeleccionar
            )
            .padding(vertical = Dimens.espacioChico),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = seleccionado, onClick = null, enabled = habilitado)
        Spacer(modifier = Modifier.width(Dimens.espacioChico))
        Column {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                color = if (habilitado) MaterialTheme.colorScheme.onSurface else GrisDeshabilitado
            )
            if (detalle != null) {
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (habilitado) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else GrisDeshabilitado
                )
            }
        }
    }
}


@Composable
fun ErrorAnimado(mensaje: String?) {
    AnimatedVisibility(visible = mensaje != null, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = mensaje ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = Dimens.espacioMedio, top = Dimens.espacioMini)
        )
    }
}


@Composable
fun ConfirmacionGuardado(visible: Boolean, mensaje: String) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            
            var aparecer by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { aparecer = true }
            val escala by animateFloatAsState(
                targetValue = if (aparecer) 1f else 0.3f,
                animationSpec = spring(dampingRatio = 0.35f),
                label = "escala"
            )
            TarjetaBase(modifier = Modifier.padding(Dimens.espacioGrande)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Exito,
                        modifier = Modifier.size(Dimens.tamanoIconoGrande).scale(escala)
                    )
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetasPreview() {
    TerraLogTheme {
        TarjetaBase {
            EncabezadoSeccion("Datos del equipo")
            FilaDato("TAG", "TR-101")
            FilaDato("Marca", "ABB")
        }
    }
}