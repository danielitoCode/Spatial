package io.github.danielitocode.spatial.buildlogic.base

open class BaseExtension {

    /**
     * Nombre del módulo mostrado en logs.
     */
    var displayName: String = ""

    /**
     * Indica si el módulo forma parte de la API pública.
     */
    var publishable: Boolean = true

    /**
     * Permite excluir módulos de validaciones.
     */
    var enableVerification: Boolean = true

}