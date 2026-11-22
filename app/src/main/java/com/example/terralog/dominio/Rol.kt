package com.example.terralog.dominio

enum class Rol(val nombre: String) {
    TECNICO("Técnico"),
    INGENIERO("Ingeniero de pruebas"),
    JEFE_TECNICO("Jefe técnico"),
    CLIENTE("Cliente");

    companion object {
        fun desde(texto: String): Rol = entries.firstOrNull { it.name == texto } ?: CLIENTE
    }
}