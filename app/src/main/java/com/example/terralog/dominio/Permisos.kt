package com.example.terralog.dominio

enum class AccionProtocolo(val texto: String) {
    REGISTRAR_EN_TERRENO("Registrar en terreno"),
    FORMALIZAR("Formalizar"),
    OBSERVAR("Observar"),
    ANULAR("Anular")
}

object Permisos {
    
    fun puedeRegistrar(rol: Rol): Boolean = rol != Rol.CLIENTE

    fun puedeFormalizar(rol: Rol): Boolean = rol == Rol.INGENIERO || rol == Rol.JEFE_TECNICO

    
    fun puedeEditar(rol: Rol, estado: EstadoProtocolo, esCreador: Boolean): Boolean {
        if (rol == Rol.CLIENTE) return false
        if (estado != EstadoProtocolo.BORRADOR && estado != EstadoProtocolo.OBSERVADO) return false
        return esCreador || puedeFormalizar(rol)
    }


    fun acciones(rol: Rol, estado: EstadoProtocolo): List<AccionProtocolo> {
        if (rol == Rol.CLIENTE) return emptyList()
        val lista = mutableListOf<AccionProtocolo>()
        if (estado == EstadoProtocolo.BORRADOR || estado == EstadoProtocolo.OBSERVADO) {
            lista.add(AccionProtocolo.REGISTRAR_EN_TERRENO)
        }
        if (estado == EstadoProtocolo.REGISTRADO_EN_TERRENO && puedeFormalizar(rol)) {
            lista.add(AccionProtocolo.FORMALIZAR)
            lista.add(AccionProtocolo.OBSERVAR)
        }
        val sePuedeAnular = estado != EstadoProtocolo.FORMALIZADO && estado != EstadoProtocolo.ANULADO
        if (sePuedeAnular && puedeFormalizar(rol)) {
            lista.add(AccionProtocolo.ANULAR)
        }
        return lista
    }
}
