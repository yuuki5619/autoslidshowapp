package jp.techacademy.satou.autoslidshowapp


import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timer

//@Suppress("UNUSED_CHANGED_VALUE")
class MainActivity : AppCompatActivity() {
    var mymap = mutableMapOf<Int, Uri>()
    var cnt = 0
    private var mHandler = Handler()
    private var mTimer: Timer? = null

    private val PERMISSIONS_REQUET_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUET_CODE
                )
            }
        } else {
            getContentsInfo()
        }

        go_button.setOnClickListener {
            if (cnt == 2) {
                cnt = 0
                imageView.setImageURI(mymap[cnt])
            } else if (cnt == 1) {
                cnt += 1
                imageView.setImageURI(mymap[cnt])
            } else if (cnt == 0) {
                cnt += 1
                imageView.setImageURI(mymap[cnt])
            } else {
                cnt == 5
                cnt = 0
                imageView.setImageURI(mymap[cnt])
            }

        }

        back_button.setOnClickListener {
            //Log.d("ANDROID", "$cnt")
            if (cnt == 0) {
                cnt = 2
                imageView.setImageURI(mymap[cnt])
            } else if (cnt == 1) {
                cnt -= 1
                imageView.setImageURI(mymap[cnt])
            } else if (cnt == 2) {
                cnt -= 1
                imageView.setImageURI(mymap[cnt])
            } else {
                cnt == 5
                cnt = 2
                imageView.setImageURI(mymap[cnt])
            }
        }

        slid_button.setOnClickListener {
            if (slid_button.text == "停止") {
                mTimer!!.cancel()
                slid_button.text = "再生"
                go_button.visibility = View.VISIBLE
                back_button.visibility = View.VISIBLE
                cnt = 5
                //Log.d("ANDROIDA", "$cnt")
            } else {
                mTimer = Timer()
                cnt = 0
                slid_button.text = "停止"
                go_button.visibility = View.INVISIBLE
                back_button.visibility = View.INVISIBLE
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            if (cnt == 3) {
                                cnt = 0
                                imageView.setImageURI(mymap[cnt])
                                cnt += 1
                            } else {
                                imageView.setImageURI(mymap[cnt])
                                //Log.d("ANDROID", "$cnt")
                                cnt += 1
                            }
                        }
                    }


                }, 2000, 2000)
            }
        }


    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUET_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                } else {
                    Toast.makeText(this, "外部ディスクへのアクセスが許可されませんでした。", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                mymap[cnt] = imageUri
                cnt += 1
            } while (cursor.moveToNext())

            cursor.close()
            cnt = 5
        }

    }
}



















