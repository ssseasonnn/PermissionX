package zlc.season.permissionxdemo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zlc.season.permissionx.request
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            launch {
                val result = request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (result) {
                    val path =
                        Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path
                    val file = File(path + "/test.txt")
                    file.createNewFile()
                }
            }
        }

        launch {
            repeat(100) {
                delay(500)
                button.text = it.toString()
            }
        }
    }
}
