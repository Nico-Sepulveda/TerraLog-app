package com.example.terralog.datos

import com.example.terralog.datos.entidades.Cliente
import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.Instalacion
import com.example.terralog.datos.entidades.Instrumento
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.entidades.Protocolo
import com.example.terralog.datos.entidades.Prueba
import com.example.terralog.datos.entidades.Usuario
import com.example.terralog.dominio.EstadoProtocolo
import com.example.terralog.dominio.Resultados
import com.example.terralog.dominio.Rol
import com.example.terralog.dominio.TipoEquipo
import com.example.terralog.dominio.TipoPrueba
import java.util.Calendar

object DatosEjemplo {

    private const val CLAVE = "terralog123"

    private fun fecha(dia: Int, mes: Int, anio: Int, hora: Int = 0, minuto: Int = 0): Long {
        val c = Calendar.getInstance()
        c.clear()
        c.set(anio, mes - 1, dia, hora, minuto, 0)
        return c.timeInMillis
    }

    suspend fun sembrar(bd: BaseDatos) {

        val clientes = listOf(
            "Envasadora del Pacífico S.A." to "76.482.915-8",
            "Sanitaria Valle Central S.A." to "96.731.540-0",
            "Minera Cerro Alto SpA" to "77.105.362-9",
            "Clínica Los Aromos Ltda." to "76.903.218-5",
            "Cementos Lomas Blancas S.A." to "99.524.170-6",
            "Data Center Andes Norte SpA" to "78.214.635-1"
        )
        val idsCliente = clientes.map { bd.clienteDao().insertar(Cliente(razonSocial = it.first, rut = it.second)) }

        val instalaciones = listOf(
            Triple("Planta Lampa", "Lampa", "Inducción obligatoria de 30 min"),
            Triple("Planta Elevadora Sur", "San Bernardo", "Sala eléctrica subterránea, sin señal"),
            Triple("Faena Til Til", "Til Til", "Sin señal en toda la faena"),
            Triple("Edificio Clínico", "La Florida", "Cortes solo domingos 06:00–10:00"),
            Triple("Planta Molienda", "Puente Alto", "Uso obligatorio de respirador"),
            Triple("Site Huechuraba", "Huechuraba", "Acompañamiento permanente del cliente")
        )
        val idsInstalacion = instalaciones.mapIndexed { i, d ->
            bd.instalacionDao().insertar(
                Instalacion(clienteId = idsCliente[i], nombre = d.first, comuna = d.second, observacionesAcceso = d.third)
            )
        }


        data class DatoEquipo(
            val tag: String, val tipo: TipoEquipo, val inst: Int, val marca: String,
            val serie: String, val anio: Int, val placa: String, val maxContactos: Double? = null
        )

        val equipos = listOf(
            DatoEquipo("TR-01", TipoEquipo.TRANSFORMADOR, 0, "Rhona", "RH-18733", 2012, "1.500 kVA, 12 / 0,4 kV"),
            DatoEquipo("TR-02", TipoEquipo.TRANSFORMADOR, 0, "Rhona", "RH-21904", 2017, "1.000 kVA, 12 / 0,4 kV"),
            DatoEquipo("CEL-MT-03", TipoEquipo.CELDA_MT, 3, "Ormazabal", "OR-77812", 2018, "12 kV, 630 A", 60.0),
            DatoEquipo("INT-52-1", TipoEquipo.INTERRUPTOR, 2, "Schneider", "SE-449021", 2015, "23 kV, 1.250 A", 45.0)
        )
        val idsEquipo = equipos.map {
            bd.equipoDao().insertar(
                Equipo(
                    instalacionId = idsInstalacion[it.inst], tag = it.tag, tipo = it.tipo.name, marca = it.marca,
                    nroSerie = it.serie, anio = it.anio, datosPlaca = it.placa, maxContactos = it.maxContactos
                )
            )
        }


        data class DatoUsuario(val nombre: String, val rut: String, val correo: String, val rol: Rol, val cliente: Int? = null)

        val usuarios = listOf(
            DatoUsuario("Felipe Araneda Quiroz", "16.739.208-3", "faraneda@induztek.cl", Rol.TECNICO),
            DatoUsuario("Camila Becerra Vidal", "18.452.961-0", "cbecerra@induztek.cl", Rol.TECNICO),
            DatoUsuario("Joaquín Salinas Bravo", "17.093.847-K", "jsalinas@induztek.cl", Rol.TECNICO),
            DatoUsuario("Valentina Ortega Campos", "19.318.624-6", "vortega@induztek.cl", Rol.TECNICO),
            DatoUsuario("Isabel Mardones Cifuentes", "14.862.570-0", "imardones@induztek.cl", Rol.INGENIERO),
            DatoUsuario("Rodrigo Pizarro Mena", "15.927.413-6", "rpizarro@induztek.cl", Rol.INGENIERO),
            DatoUsuario("Héctor Bustamante Toro", "13.486.092-8", "hbustamante@induztek.cl", Rol.JEFE_TECNICO),
            DatoUsuario("Paola Villalobos Cárdenas", "16.204.835-K", "pvillalobos@envpacifico.cl", Rol.CLIENTE, 0),
            DatoUsuario("Gonzalo Sepúlveda Ibarra", "18.776.351-7", "gsepulveda@losaromos.cl", Rol.CLIENTE, 3),
            DatoUsuario("Natalia Escobar Fierro", "12.950.684-9", "nescobar@cerroalto.cl", Rol.CLIENTE, 2)
        )
        val idsUsuario = usuarios.map {
            bd.usuarioDao().insertar(
                Usuario(
                    nombre = it.nombre, rut = it.rut, correo = it.correo, clave = CLAVE,
                    rol = it.rol.name, clienteId = it.cliente?.let { i -> idsCliente[i] }
                )
            )
        }


        val instrumentos = listOf(
            Instrumento(nombre = "Megóhmetro 5 kV", marcaModelo = "Megger MIT525", nroSerie = "MIT-0045821", vencimientoCalibracion = fecha(15, 3, 2027)),
            Instrumento(nombre = "Megóhmetro 10 kV", marcaModelo = "Megger MIT1025", nroSerie = "MIT-0071233", vencimientoCalibracion = fecha(10, 10, 2026)),
            Instrumento(nombre = "Medidor de relación (TTR)", marcaModelo = "Megger TTR300", nroSerie = "TTR-30044", vencimientoCalibracion = fecha(2, 6, 2027)),
            Instrumento(nombre = "Micro-ohmímetro", marcaModelo = "Megger DLRO10X", nroSerie = "DLR-10882", vencimientoCalibracion = fecha(20, 8, 2026)),
            Instrumento(nombre = "Equipo de inyección secundaria", marcaModelo = "Omicron CMC 356", nroSerie = "CMC-HB411", vencimientoCalibracion = fecha(5, 1, 2027))
        )
        val idsInstrumento = instrumentos.map { bd.instrumentoDao().insertar(it) }


        data class Historial(
            val numero: String, val dia: Int, val mes: Int, val anio: Int, val tecnico: Int,
            val temperatura: Double, val humedad: Double, val estado: EstadoProtocolo,
            val lectura1: Double, val lectura10: Double
        )

        val historial = listOf(
            Historial("PT-2024-0312", 14, 9, 2024, 0, 18.0, 55.0, EstadoProtocolo.FORMALIZADO, 12500.0, 31250.0),
            Historial("PT-2025-0288", 13, 9, 2025, 1, 21.0, 48.0, EstadoProtocolo.FORMALIZADO, 9800.0, 21560.0),
            Historial("PT-2026-0401", 26, 9, 2026, 0, 19.0, 60.0, EstadoProtocolo.REGISTRADO_EN_TERRENO, 6100.0, 10980.0)
        )
        for (h in historial) {
            val fechaPrueba = fecha(h.dia, h.mes, h.anio, 10, 30)
            val formalizado = h.estado == EstadoProtocolo.FORMALIZADO
            val valores = mapOf("lectura1" to h.lectura1, "lectura10" to h.lectura10, "tension" to 5.0)
            val calculo = Resultados.calcular(TipoPrueba.AISLACION_10_MIN, valores, null)

            val protocoloId = bd.protocoloDao().insertar(
                Protocolo(
                    numero = h.numero, equipoId = idsEquipo[0], tecnicoId = idsUsuario[h.tecnico],
                    fechaHora = fechaPrueba, temperatura = h.temperatura, humedad = h.humedad,
                    estado = h.estado.name, resultadoGlobal = calculo.resultado?.name,
                    formalizadoPorId = if (formalizado) idsUsuario[4] else null,
                    fechaFormalizacion = if (formalizado) fechaPrueba + 86_400_000L else null
                )
            )
            val pruebaId = bd.pruebaDao().insertar(
                Prueba(
                    protocoloId = protocoloId, tipoPrueba = TipoPrueba.AISLACION_10_MIN.name,
                    instrumentoId = idsInstrumento[0], realizada = true, resultado = calculo.resultado?.name,
                    comentario = "Devanado AT-T a 5 kV"
                )
            )
            val mediciones = TipoPrueba.AISLACION_10_MIN.campos.map {
                Medicion(pruebaId = pruebaId, clave = it.clave, nombre = it.nombre, valor = valores.getValue(it.clave), unidad = it.unidad)
            } + calculo.calculados.map {
                Medicion(pruebaId = pruebaId, clave = it.clave, nombre = it.nombre, valor = it.valor, unidad = it.unidad, calculado = true)
            }
            bd.pruebaDao().insertarMediciones(mediciones)
        }
    }
}