package com.example.terralog.ui.navegacion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.terralog.datos.Sesion
import com.example.terralog.ui.pantallas.DetalleEquipoPantalla
import com.example.terralog.ui.pantallas.DetalleProtocoloPantalla
import com.example.terralog.ui.pantallas.ListaEquiposPantalla
import com.example.terralog.ui.pantallas.LoginPantalla
import com.example.terralog.ui.pantallas.NuevoProtocoloPantalla
import com.example.terralog.ui.pantallas.RegistrarEquipoPantalla


object Rutas {
    const val LOGIN = "login"
    const val EQUIPOS = "equipos"
    const val REGISTRAR_EQUIPO = "registrar_equipo"
    const val EQUIPO = "equipo/{equipoId}"
    const val NUEVO_PROTOCOLO = "nuevo_protocolo/{equipoId}"
    const val PROTOCOLO = "protocolo/{protocoloId}"

    fun equipo(id: Long) = "equipo/$id"
    fun nuevoProtocolo(equipoId: Long) = "nuevo_protocolo/$equipoId"
    fun protocolo(id: Long) = "protocolo/$id"
}

private const val DURACION = 450

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION)) + fadeIn(tween(DURACION))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION)) + fadeOut(tween(DURACION))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION)) + fadeIn(tween(DURACION))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION)) + fadeOut(tween(DURACION))
        }
    ) {
        composable(Rutas.LOGIN) {
            LoginPantalla(
                alIngresar = {
                    navController.navigate(Rutas.EQUIPOS) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.EQUIPOS) {
            ListaEquiposPantalla(
                alAgregar = { navController.navigate(Rutas.REGISTRAR_EQUIPO) },
                alVerEquipo = { navController.navigate(Rutas.equipo(it)) },
                alCerrarSesion = {
                    Sesion.cerrar()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.REGISTRAR_EQUIPO) {
            RegistrarEquipoPantalla(alVolver = { navController.popBackStack() })
        }

        composable(
            route = Rutas.EQUIPO,
            arguments = listOf(navArgument("equipoId") { type = NavType.LongType })
        ) { entrada ->
            val equipoId = entrada.arguments?.getLong("equipoId") ?: 0L
            DetalleEquipoPantalla(
                equipoId = equipoId,
                alVolver = { navController.popBackStack() },
                alIniciarProtocolo = { navController.navigate(Rutas.nuevoProtocolo(it)) },
                alVerProtocolo = { navController.navigate(Rutas.protocolo(it)) }
            )
        }

        composable(
            route = Rutas.NUEVO_PROTOCOLO,
            arguments = listOf(navArgument("equipoId") { type = NavType.LongType })
        ) { entrada ->
            val equipoId = entrada.arguments?.getLong("equipoId") ?: 0L
            NuevoProtocoloPantalla(
                equipoId = equipoId,
                alVolver = { navController.popBackStack() },
                alCrear = { protocoloId ->

                    navController.navigate(Rutas.protocolo(protocoloId)) {
                        popUpTo(Rutas.NUEVO_PROTOCOLO) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.PROTOCOLO,
            arguments = listOf(navArgument("protocoloId") { type = NavType.LongType })
        ) { entrada ->
            val protocoloId = entrada.arguments?.getLong("protocoloId") ?: 0L
            DetalleProtocoloPantalla(
                protocoloId = protocoloId,
                alVolver = { navController.popBackStack() }
            )
        }
    }
}