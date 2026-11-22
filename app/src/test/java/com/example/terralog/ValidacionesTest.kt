package com.example.terralog

import com.example.terralog.dominio.AccionProtocolo
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Permisos
import com.example.terralog.dominio.Rol
import com.example.terralog.dominio.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun correo_valido_e_invalido() {
        assertNull(Validaciones.validarCorreo("mgodoy@induztek.ejemplo"))
        assertNotNull(Validaciones.validarCorreo(""))
        assertNotNull(Validaciones.validarCorreo("sin-arroba"))
    }

    @Test
    fun clave_minimo_6_caracteres() {
        assertNotNull(Validaciones.validarClave("12345"))
        assertNull(Validaciones.validarClave("clave123"))
    }

    @Test
    fun tag_acepta_formatos_del_caso_y_rechaza_repetidos() {
        assertNull(Validaciones.validarTag("TR-01", emptyList()))
        assertNull(Validaciones.validarTag("REL-50/51-A", emptyList()))
        assertNotNull(Validaciones.validarTag("", emptyList()))
        assertNotNull(Validaciones.validarTag("TR 01", emptyList()))

        assertNotNull(Validaciones.validarTag("tr-01", listOf("TR-01", "TR-02")))
    }

    @Test
    fun numero_acepta_coma_decimal_y_valida_el_rango() {
        assertEquals(1.8, Validaciones.aNumero("1,8")!!, 0.0001)
        assertNull(Validaciones.aNumero("abc"))
        assertNull(Validaciones.aNumero("NaN"))
        assertNull(Validaciones.validarNumero("25,5", -20.0, 60.0, "°C"))
        assertNotNull(Validaciones.validarNumero("-50", -20.0, 60.0, "°C"))
        assertNotNull(Validaciones.validarNumero("", 0.0, 100.0, "%"))

        assertNotNull(Validaciones.validarNumero("-5", 0.1, 1000000.0, "MΩ"))
    }

    @Test
    fun anio_dentro_del_rango() {
        assertNull(Validaciones.validarAnio("2012"))
        assertNotNull(Validaciones.validarAnio("1900"))
        assertNotNull(Validaciones.validarAnio("abc"))
        assertNotNull(Validaciones.validarAnio(""))
    }

    @Test
    fun instrumento_vencido_no_se_puede_usar() {

        val vence = 1_000_000_000_000L
        assertFalse(Validaciones.estaVencido(vence, vence + 1000))
        assertTrue(Validaciones.estaVencido(vence, vence + 86_400_000L))
        assertNull(Validaciones.validarInstrumento(vence, vence - 1000))
        assertNotNull(Validaciones.validarInstrumento(vence, vence + 90_000_000L))
        assertNotNull(Validaciones.validarInstrumento(null, vence))
    }

    @Test
    fun motivo_obligatorio_de_al_menos_5_caracteres() {
        assertNotNull(Validaciones.validarMotivo(""))
        assertNotNull(Validaciones.validarMotivo("no"))
        assertNull(Validaciones.validarMotivo("cliente no autorizó desconexión"))
    }

    @Test
    fun permisos_segun_el_rol_y_el_estado() {

        assertFalse(Permisos.puedeFormalizar(Rol.TECNICO))
        assertTrue(Permisos.puedeFormalizar(Rol.INGENIERO))

        assertFalse(Permisos.puedeRegistrar(Rol.CLIENTE))
        assertTrue(Permisos.acciones(Rol.CLIENTE, EstadoProtocolo.REGISTRADO_EN_TERRENO).isEmpty())

        assertTrue(Permisos.acciones(Rol.JEFE_TECNICO, EstadoProtocolo.FORMALIZADO).isEmpty())

        val acciones = Permisos.acciones(Rol.INGENIERO, EstadoProtocolo.REGISTRADO_EN_TERRENO)
        assertTrue(acciones.containsAll(listOf(AccionProtocolo.FORMALIZAR, AccionProtocolo.OBSERVAR, AccionProtocolo.ANULAR)))

        assertTrue(Permisos.puedeEditar(Rol.TECNICO, EstadoProtocolo.BORRADOR, esCreador = true))
        assertFalse(Permisos.puedeEditar(Rol.TECNICO, EstadoProtocolo.BORRADOR, esCreador = false))
        assertFalse(Permisos.puedeEditar(Rol.TECNICO, EstadoProtocolo.REGISTRADO_EN_TERRENO, esCreador = true))
    }

    @Test
    fun cliente_ve_pendiente_de_formalizacion_hasta_que_se_formaliza() {

        assertEquals("Pendiente de formalización", EstadoProtocolo.REGISTRADO_EN_TERRENO.nombreParaCliente())
        assertEquals("Pendiente de formalización", EstadoProtocolo.OBSERVADO.nombreParaCliente())
        assertEquals("Formalizado", EstadoProtocolo.FORMALIZADO.nombreParaCliente())
        assertFalse(EstadoProtocolo.visiblesParaCliente.contains(EstadoProtocolo.BORRADOR))
        assertFalse(EstadoProtocolo.visiblesParaCliente.contains(EstadoProtocolo.ANULADO))
    }
}
