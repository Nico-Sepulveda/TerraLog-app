package com.example.terralog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.terralog.ui.navegacion.NavegacionApp
import com.example.terralog.ui.theme.TerraLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TerraLogTheme {
                NavegacionApp()
            }
        }
    }
}