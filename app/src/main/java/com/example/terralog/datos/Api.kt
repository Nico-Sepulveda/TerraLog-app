package com.example.terralog.datos

import com.example.terralog.datos.entidades.Cliente
import com.example.terralog.datos.entidades.Equipo
import com.example.terralog.datos.entidades.Instalacion
import com.example.terralog.datos.entidades.Medicion
import com.example.terralog.datos.entidades.Prueba
import com.example.terralog.datos.entidades.Usuario
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiUsuarios {
    @POST("usuarios/login")
    suspend fun iniciarSesion(@Body datos: Map<String, String>): Usuario

    @POST("usuarios/restablecer")
    suspend fun restablecer(@Body datos: Map<String, String>): retrofit2.Response<Unit>
}

interface ApiClientes {
    @GET("clientes")
    suspend fun clientes(): List<Cliente>

    @GET("instalaciones")
    suspend fun instalaciones(): List<Instalacion>
}

interface ApiEquipos {
    @GET("equipos")
    suspend fun todos(): List<Equipo>

    @GET("equipos")
    suspend fun deCliente(@Query("clienteId") clienteId: Long): List<Equipo>

    @GET("equipos")
    suspend fun deInstalacion(@Query("instalacionId") instalacionId: Long): List<Equipo>

    @GET("equipos/{id}")
    suspend fun equipo(@Path("id") id: Long): Equipo

    @POST("equipos")
    suspend fun crear(@Body datos: @JvmSuppressWildcards Map<String, Any?>): Equipo
}

class InstrumentoDto(
    val id: Long,
    val nombre: String,
    val marcaModelo: String,
    val nroSerie: String,
    val vencimientoCalibracion: String
)

interface ApiInstrumentos {
    @GET("instrumentos")
    suspend fun todos(): List<InstrumentoDto>
}

class ProtocoloDto(
    val id: Long,
    val numero: String,
    val equipoId: Long,
    val tecnicoId: Long,
    val fechaHora: String,
    val temperatura: Double,
    val humedad: Double,
    val estado: String,
    val resultadoGlobal: String?,
    val motivo: String?,
    val comentarioIngeniero: String?,
    val formalizadoPorId: Long?,
    val fechaFormalizacion: String?
)

interface ApiProtocolos {
    @GET("protocolos")
    suspend fun deEquipo(@Query("equipoId") equipoId: Long, @Query("rol") rol: String): List<ProtocoloDto>

    @GET("protocolos/{id}")
    suspend fun protocolo(@Path("id") id: Long): ProtocoloDto

    @POST("protocolos")
    suspend fun crear(@Body datos: @JvmSuppressWildcards Map<String, Any?>): ProtocoloDto

    @POST("usuarios/restablecer")
    suspend fun restablecer(@Body datos: Map<String, String>): retrofit2.Response<Unit>

    @PATCH("protocolos/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body datos: @JvmSuppressWildcards Map<String, Any?>): ResponseBody
}

class PruebaDetalle(val prueba: Prueba, val mediciones: List<Medicion>)

interface ApiPruebas {
    @GET("pruebas")
    suspend fun deProtocolo(@Query("protocoloId") protocoloId: Long): List<Prueba>

    @GET("pruebas/{id}")
    suspend fun detalle(@Path("id") id: Long): PruebaDetalle
}

object Api {
    const val SERVIDOR = "192.168.1.82"

    private fun crear(puerto: Int) = Retrofit.Builder()
        .baseUrl("http://$SERVIDOR:$puerto/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarios: ApiUsuarios = crear(8081).create(ApiUsuarios::class.java)
    val clientes: ApiClientes = crear(8082).create(ApiClientes::class.java)
    val equipos: ApiEquipos = crear(8083).create(ApiEquipos::class.java)
    val instrumentos: ApiInstrumentos = crear(8084).create(ApiInstrumentos::class.java)
    val protocolos: ApiProtocolos = crear(8085).create(ApiProtocolos::class.java)
    val pruebas: ApiPruebas = crear(8086).create(ApiPruebas::class.java)
}