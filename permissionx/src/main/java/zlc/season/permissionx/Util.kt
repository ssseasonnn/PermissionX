package zlc.season.permissionx

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import zlc.season.claritypotion.ClarityPotion.Companion.clarityPotion
import zlc.season.claritypotion.ClarityPotion.Companion.currentActivity

internal fun checkPermission(permission: String): Boolean {
    return checkSelfPermission(clarityPotion, permission) == PERMISSION_GRANTED
}

internal fun checkPermissions(permissions: Array<out String>): Boolean {
    var result = true
    permissions.forEach {
        if (!checkPermission(it)) {
            result = false
        }
    }
    return result
}

internal suspend fun exec(request: Request): Result {
    when (val currentActivity = currentActivity()) {
        null -> throw RuntimeException("Activity not found!")
        !is FragmentActivity -> throw RuntimeException("Activity should inherit FragmentActivity!")
        else -> {
            val fm = currentActivity.supportFragmentManager
            if (fm.isDestroyed) {
                throw RuntimeException("Activity already finished!")
            } else {
                val prevOrientation = currentActivity.requestedOrientation
                return VirtualFragment.showAsFlow(fm, request)
                    .onStart { currentActivity.lockOrientation() }
                    .onCompletion { currentActivity.restoreOrientation(prevOrientation) }
                    .first()
            }
        }
    }
}

private fun FragmentActivity.lockOrientation() {
    requestedOrientation = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else -> ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
    }
}

private fun FragmentActivity.restoreOrientation(preOrientation: Int) {
    requestedOrientation = preOrientation
}