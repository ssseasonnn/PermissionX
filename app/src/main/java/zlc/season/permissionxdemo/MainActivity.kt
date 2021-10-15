package zlc.season.permissionxdemo

import android.Manifest.permission.*
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import zlc.season.permissionx.request
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_storage).setOnClickListener {
            lifecycleScope.launch {
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

        findViewById<View>(R.id.btn_camera).setOnClickListener {
            lifecycleScope.launch {
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
        println("destroy")
    }
}
