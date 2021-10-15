package zlc.season.permissionx

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive

class VirtualFragment : Fragment() {
    companion object {
        private const val TAG = "zlc.season.permissionx.VirtualFragment"

        private fun FragmentManager.add(fragment: Fragment) {
            beginTransaction().add(fragment, TAG).commitNowAllowingStateLoss()
        }

        private fun FragmentManager.remove(fragment: Fragment) {
            beginTransaction().remove(fragment).commitNowAllowingStateLoss()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun showAsFlow(fm: FragmentManager, request: Request) = callbackFlow {
            val fragment = VirtualFragment()
            val cb = object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    if (fragment === f) {
                        fragment.callback = {
                            trySend(it)
                            fm.remove(fragment)
                            close()
                        }
                        fragment.launcher.launch(request.permissions)
                    }
                }

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    if (fragment === f) {
                        close()
                    }
                }
            }

            if (isActive) {
                fm.registerFragmentLifecycleCallbacks(cb, false)
                fm.add(fragment)
            }

            awaitClose {
                fm.unregisterFragmentLifecycleCallbacks(cb)
            }
        }
    }

    var callback: (Result) -> Unit = {}

    val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var isGranted = true
        var shouldShowRational = false
        var alwaysDenied = false
        var deniedPermission = ""

        for (each in it) {
            val permissionName = each.key
            val flag = each.value
            if (!flag) {
                isGranted = false
                deniedPermission = permissionName
                val rationale = shouldShowRequestPermissionRationale(deniedPermission)
                if (rationale) {
                    shouldShowRational = true
                } else {
                    alwaysDenied = true
                }
                break
            }
        }

        val result = Result(
            isGranted,
            shouldShowRational,
            alwaysDenied,
            deniedPermission
        )

        callback(result)
    }
}
