package zlc.season.permissionxdemo

import android.Manifest.permission.*
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import zlc.season.permissionx.request
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_storage.setOnClickListener {
            launch {
                try {
                    val result = request(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                    Log.d("TAG", result.toString())
                    if (result.isGranted) {
                        withContext(Dispatchers.IO) {
                            val path = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path
                            val file = File("$path/test.txt")
                            file.createNewFile()
                        }
                    }
                } catch (t: Throwable) {
                    Log.w("TAG", t)
                }
            }
        }

        btn_camera.setOnClickListener {
            launch {
                try {
                    val result = request(CAMERA)
                    Log.d("TAG", result.toString())
                    if (result.isGranted) {
                        //open camera
                    }
                } catch (t: Throwable) {
                    Log.w("TAG", t)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
