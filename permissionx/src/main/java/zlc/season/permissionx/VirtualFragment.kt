package zlc.season.permissionx

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class VirtualFragment : Fragment() {
    companion object {
        private const val TAG = "VirtualFragment"
        private const val PARAM = "PARAM"

        fun open(fm: FragmentManager, request: Request) {
            val bridgeFragment = fm.findFragmentByTag(TAG)
            if (bridgeFragment != null) {
                (bridgeFragment as VirtualFragment).realExec(request)
            } else {
                VirtualFragment().apply {
                    arguments = Bundle().apply { putParcelable(PARAM, request) }
                }.also {
                    fm.beginTransaction().add(it, TAG).commitAllowingStateLoss()
                }
            }
        }
    }

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val request = it.getParcelable<Request>(PARAM)!!
            realExec(request)
        }
    }

    private fun realExec(request: Request) {
        if (checkPermissions(request.permissions)) {
            resume(request, Result(true))
        } else {
            requestPermissions(request.permissions, request.requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var isGranted = true
        var shouldShowRational = false
        var alwaysDenied = false
        var deniedPermission = ""

        for (i in grantResults.indices) {
            if (grantResults[i] != PERMISSION_GRANTED) {
                isGranted = false
                deniedPermission = permissions[i]
                val rationale = shouldShowRequestPermissionRationale(deniedPermission)
                if (rationale) {
                    shouldShowRational = true
                } else {
                    alwaysDenied = true
                }
                break
            }
        }

        resume(
            requestCode, Result(
                isGranted,
                shouldShowRational,
                alwaysDenied,
                deniedPermission
            )
        )
    }
}
