package com.example.terralog.recursos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


object Camara {

    suspend fun guardarFoto(contexto: Context, foto: Bitmap): String = withContext(Dispatchers.IO) {
        val carpeta = File(contexto.filesDir, "fotos")
        carpeta.mkdirs()
        val archivo = File(carpeta, "placa_${System.currentTimeMillis()}.jpg")
        FileOutputStream(archivo).use { salida ->
            foto.compress(Bitmap.CompressFormat.JPEG, 90, salida)
        }
        archivo.absolutePath
    }


    fun cargarFoto(ruta: String): ImageBitmap? = BitmapFactory.decodeFile(ruta)?.asImageBitmap()
}