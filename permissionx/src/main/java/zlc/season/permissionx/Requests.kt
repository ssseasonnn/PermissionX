package zlc.season.permissionx

class Result(
    val isGranted: Boolean,
    val shouldShowRational: Boolean = false,
    val alwaysDenied: Boolean = false,
    val deniedPermission: String = ""
) {
    override fun toString(): String {
        return """
            Result[
                isGranted=$isGranted,
                shouldShowRational=$shouldShowRational,
                alwaysDenied=$alwaysDenied,
                deniedPermission=$deniedPermission
            ]
        """.trimMargin()
    }
}

class Request(val permissions: Array<out String>)