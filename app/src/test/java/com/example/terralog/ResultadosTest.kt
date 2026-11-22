package com.example.terralog

import com.example.terralog.dominio.Resultado
import com.example.terralog.dominio.Resultados
import com.example.terralog.dominio.TipoPrueba
import com.example.terralog.dominio.Variacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultadosTest {

    private fun aislacion(lectura1: Double, lectura10: Double) = Resultados.calcular(
        TipoPrueba.AISLACION_10_MIN,
        mapOf("lectura1" to lectura1, "lectura10" to lectura10, "tension" to 5.0),
        null
    )

    @Test
    fun indiceDePolarizacion_1coma6_queda_observado() {
        val r = aislacion(1000.0, 1600.0)
        assertEquals(Resultado.OBSERVADO, r.resultado)
        assertEquals(1.6, r.calculados.first().valor, 0.001)
    }

    @Test
    fun indiceDePolarizacion_2_o_mas_queda_aprobado() {
        assertEquals(Resultado.APROBADO, aislacion(12500.0, 31250.0).resultado)
    }

    @Test
    fun indiceDePolarizacion_menor_a_1_queda_rechazado() {
        assertEquals(Resultado.RECHAZADO, aislacion(1000.0, 900.0).resultado)
    }

    @Test
    fun lecturaMenorA100_queda_rechazada_aunque_el_IP_sea_bueno() {
        assertEquals(Resultado.RECHAZADO, aislacion(50.0, 200.0).resultado)
    }

    @Test
    fun errorTTR_se_clasifica_segun_los_limites() {
        fun ttr(medida: Double) = Resultados.calcular(
            TipoPrueba.TTR, mapOf("relacionTeorica" to 100.0, "relacionMedida" to medida, "posicion" to 1.0), null
        ).resultado
        assertEquals(Resultado.APROBADO, ttr(100.4))
        assertEquals(Resultado.OBSERVADO, ttr(100.8))
        assertEquals(Resultado.RECHAZADO, ttr(102.0))
    }

    @Test
    fun resistenciaDeContactos_se_compara_con_el_maximo_de_placa() {
        fun contactos(valor: Double) = Resultados.calcular(
            TipoPrueba.CONTACTOS, mapOf("polo1" to valor, "polo2" to 10.0, "polo3" to 10.0), 60.0
        ).resultado
        assertEquals(Resultado.APROBADO, contactos(60.0))
        assertEquals(Resultado.OBSERVADO, contactos(70.0))
        assertEquals(Resultado.RECHAZADO, contactos(80.0))
    }

    @Test
    fun releQueNoOpera_queda_rechazado() {
        val r = Resultados.calcular(
            TipoPrueba.OPERACION_RELE, mapOf("ajuste" to 5.0, "tiempoEsperado" to 100.0, "opera" to 0.0), null
        )
        assertEquals(Resultado.RECHAZADO, r.resultado)
    }

    @Test
    fun tiempoDelRele_se_clasifica_segun_la_diferencia() {
        fun rele(medido: Double) = Resultados.calcular(
            TipoPrueba.OPERACION_RELE,
            mapOf("ajuste" to 5.0, "corrienteMedida" to 5.0, "tiempoEsperado" to 100.0, "tiempoMedido" to medido, "opera" to 1.0),
            null
        ).resultado
        assertEquals(Resultado.APROBADO, rele(104.0))
        assertEquals(Resultado.OBSERVADO, rele(108.0))
        assertEquals(Resultado.RECHAZADO, rele(120.0))
    }

    @Test
    fun resistenciaDeDevanados_no_tiene_resultado_automatico() {
        val r = Resultados.calcular(
            TipoPrueba.DEVANADOS, mapOf("faseA" to 1.0, "faseB" to 1.0, "faseC" to 1.0), null
        )
        assertNull(r.resultado)
    }

    @Test
    fun resultadoGlobal_es_el_peor_de_las_pruebas() {
        val peor = Resultado.peor(listOf(Resultado.APROBADO, Resultado.RECHAZADO, Resultado.OBSERVADO))
        assertEquals(Resultado.RECHAZADO, peor)
        assertNull(Resultado.peor(emptyList()))
    }

    @Test
    fun variacion_menos37coma8_es_significativa_como_en_el_ejemplo_del_caso() {
        val v = Variacion.porcentual(6100.0, 9800.0)
        assertNotNull(v)
        assertEquals(-37.76, v!!, 0.01)
        assertTrue(Variacion.esSignificativa(v))
    }

    @Test
    fun variacion_menos21coma6_no_es_significativa() {
        val v = Variacion.porcentual(9800.0, 12500.0)!!
        assertEquals(-21.6, v, 0.01)
        assertFalse(Variacion.esSignificativa(v))
    }
}