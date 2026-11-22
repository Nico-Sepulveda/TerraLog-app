package com.example.terralog.ui.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Resultado
import com.example.terralog.ui.theme.Advertencia
import com.example.terralog.ui.theme.AdvertenciaFondo
import com.example.terralog.ui.theme.Exito
import com.example.terralog.ui.theme.ExitoFondo
import com.example.terralog.ui.theme.GrisFondo
import com.example.terralog.ui.theme.GrisTexto
import com.example.terralog.ui.theme.Peligro
import com.example.terralog.ui.theme.PeligroFondo
import com.example.terralog.ui.theme.PrimarioClaro
import com.example.terralog.ui.theme.PrimarioOscuro
import com.example.terralog.ui.theme.TerraLogTheme


@Composable
private fun Etiqueta(texto: String, fondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    val fondoAnimado by animateColorAsState(targetValue = fondo, animationSpec = tween(700), label = "fondo")
    val textoAnimado by animateColorAsState(targetValue = colorTexto, animationSpec = tween(700), label = "texto")
    Surface(shape = RoundedCornerShape(50), color = fondoAnimado, modifier = modifier) {
        Text(
            text = texto,
            color = textoAnimado,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ChipResultado(resultado: Resultado?, modifier: Modifier = Modifier) {
    when (resultado) {
        Resultado.APROBADO -> Etiqueta(Resultado.APROBADO.nombre, ExitoFondo, Exito, modifier)
        Resultado.OBSERVADO -> Etiqueta(Resultado.OBSERVADO.nombre, AdvertenciaFondo, Advertencia, modifier)
        Resultado.RECHAZADO -> Etiqueta(Resultado.RECHAZADO.nombre, PeligroFondo, Peligro, modifier)
        null -> Etiqueta("Sin resultado", GrisFondo, GrisTexto, modifier)
    }
}

@Composable
fun ChipEstado(estado: EstadoProtocolo, paraCliente: Boolean, modifier: Modifier = Modifier) {
    val texto = if (paraCliente) estado.nombreParaCliente() else estado.nombre
    when (estado) {
        EstadoProtocolo.BORRADOR -> Etiqueta(texto, GrisFondo, GrisTexto, modifier)
        EstadoProtocolo.REGISTRADO_EN_TERRENO -> Etiqueta(texto, PrimarioClaro, PrimarioOscuro, modifier)
        EstadoProtocolo.OBSERVADO -> Etiqueta(texto, AdvertenciaFondo, Advertencia, modifier)
        EstadoProtocolo.FORMALIZADO -> Etiqueta(texto, ExitoFondo, Exito, modifier)
        EstadoProtocolo.ANULADO -> Etiqueta(texto, PeligroFondo, Peligro, modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun ChipsPreview() {
    TerraLogTheme {
        ChipResultado(Resultado.APROBADO)
    }
}