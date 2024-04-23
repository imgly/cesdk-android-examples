package ly.img.editor.base.engine

import ly.img.engine.EditorApi

fun EditorApi.setRoleButPreserveGlobalScopes(role: String) {
    val scopes = findAllScopes().associateWith {
        getGlobalScope(it)
    }

    setRole(role)
    scopes.forEach { entry ->
        setGlobalScope(key = entry.key, globalScope = entry.value)
    }
}