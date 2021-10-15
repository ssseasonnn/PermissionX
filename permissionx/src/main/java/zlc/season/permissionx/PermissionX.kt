package zlc.season.permissionx

fun hasPermission(permission: String): Boolean {
    return checkPermission(permission)
}

fun hasPermissions(vararg permission: String): Boolean {
    return checkPermissions(permission)
}

suspend fun request(vararg permission: String): Result {
    val request = Request(permission)
    return exec(request)
}

