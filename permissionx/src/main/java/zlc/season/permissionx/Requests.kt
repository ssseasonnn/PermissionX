package zlc.season.permissionx

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CancellableContinuation
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

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

@Parcelize
class Request(
    val permissions: Array<out String>,
    val requestCode: Int = CODE.incrementAndGet()
) : Parcelable {
    companion object {
        private val CODE = AtomicInteger(0)
    }
}

internal object RequestPool {
    private val map = ConcurrentHashMap<Int, CancellableContinuation<Result>>()

    fun add(request: Request, continuation: CancellableContinuation<Result>) {
        map[request.requestCode] = continuation
    }

    fun get(request: Request): CancellableContinuation<Result>? {
        return map[request.requestCode]
    }

    fun get(requestCode: Int): CancellableContinuation<Result>? {
        return map[requestCode]
    }

    fun remove(request: Request) {
        map.remove(request.requestCode)
    }
}
