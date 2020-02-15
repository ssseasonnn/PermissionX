package zlc.season.permissionx

import android.os.Parcelable
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import zlc.season.claritypotion.ClarityPotion.Companion.clarityPotion
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

fun hasPermission(permission: String): Boolean {
    return checkSelfPermission(clarityPotion, permission) == PERMISSION_GRANTED
}

suspend fun request(permission: String) = suspendCancellableCoroutine<Boolean> {
    val request = Request(permission)
    RequestPool.add(request, it)
    BridgeFragment.begin(request)
    it.invokeOnCancellation { RequestPool.remove(request) }
}

@Parcelize
class Request(
    val permission: String,
    val requestCode: Int = count.incrementAndGet()
) : Parcelable {
    companion object {
        var count = AtomicInteger(0)
    }
}

object RequestPool {
    private val map = ConcurrentHashMap<Int, CancellableContinuation<Boolean>>()

    fun add(request: Request, continuation: CancellableContinuation<Boolean>) {
        map[request.requestCode] = continuation
    }

    fun get(request: Request): CancellableContinuation<Boolean> {
        return map[request.requestCode] ?: throw IllegalStateException("Never happen")
    }

    fun get(int: Int): CancellableContinuation<Boolean> {
        return map[int] ?: throw IllegalStateException("Never happen")
    }

    fun remove(request: Request) {
        map.remove(request.requestCode)
    }
}