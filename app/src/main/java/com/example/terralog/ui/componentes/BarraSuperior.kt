package com.example.terralog.ui.componentes

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.terralog.R
import com.example.terralog.ui.theme.Blanco
import com.example.terralog.ui.theme.Primario
import com.example.terralog.ui.theme.TerraLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    alVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (alVolver != null) {
                IconButton(onClick = alVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_volver))
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primario,
            titleContentColor = Blanco,
            navigationIconContentColor = Blanco,
            actionIconContentColor = Blanco
        )
    )
}

@Preview
@Composable
fun BarraSuperiorPreview() {
    TerraLogTheme {
        BarraSuperior(titulo = "Equipos", alVolver = {})
    }
}