package zlc.season.permissionx

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.claritypotion.ClarityPotion
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class BridgeFragment : Fragment() {
    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val request = it.getParcelable<Request>(PARAM_KEY)!!
            realRequestPermission(request)
        }
    }

    fun realRequestPermission(request: Request) {
        if (!hasPermission(request.permission)) {
            val array = arrayOf(request.permission)
            requestPermissions(array, request.requestCode)
        } else {
            notifySuccess(request)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PERMISSION_GRANTED) {
            notifySuccess(requestCode)
        } else {
            notifyFailed(requestCode, "Permission denied")
        }
    }


    companion object {
        private const val BRIDGE_TAG = "BRIDGE_TAG"
        private const val PARAM_KEY = "PARAM_KEY"

        private fun notifySuccess(request: Request) {
            notifySuccess(request.hashCode())
        }

        private fun notifySuccess(int: Int) {
            val continuation = RequestPool.get(int)
            if (continuation.isActive) {
                continuation.resumeWith(success(true))
            }
        }

        private fun notifyFailed(request: Request, failedMsg: String) {
            notifyFailed(request.hashCode(), failedMsg)
        }

        private fun notifyFailed(int: Int, failedMsg: String) {
            val continuation = RequestPool.get(int)
            if (continuation.isActive) {
                continuation.resumeWith(failure(RuntimeException(failedMsg)))
            }
        }

        fun begin(request: Request) {
            val currentActivity = ClarityPotion.currentActivity()
            if (currentActivity == null || currentActivity !is FragmentActivity) {
                notifyFailed(request, "No Found Any Active Activity!")
            } else {
                val fm = currentActivity.supportFragmentManager
                if (fm.isDestroyed) {
                    notifyFailed(request, "No Found Any Active Activity!")
                } else {
                    if (fm.findFragmentByTag(BRIDGE_TAG) != null) {
                        val bridgeFragment = fm.findFragmentByTag(BRIDGE_TAG) as BridgeFragment
                        bridgeFragment.realRequestPermission(request)
                    } else {
                        val bridgeFragment = BridgeFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(PARAM_KEY, request)
                            }
                        }
                        fm.beginTransaction().add(bridgeFragment, BRIDGE_TAG)
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }
}
