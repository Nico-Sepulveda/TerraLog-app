package com.example.terralog.recursos

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager


object Vibracion {

    fun exito(contexto: Context) = vibrar(contexto, longArrayOf(0, 200))

    fun error(contexto: Context) = vibrar(contexto, longArrayOf(0, 110, 90, 110))

    private fun vibrar(contexto: Context, patron: LongArray) {
        val vibrador = obtenerVibrador(contexto) ?: return
        if (!vibrador.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrador.vibrate(VibrationEffect.createWaveform(patron, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrador.vibrate(patron, -1)
        }
    }

    private fun obtenerVibrador(contexto: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val administrador = contexto.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            administrador?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            contexto.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}