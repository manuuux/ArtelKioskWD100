package com.artel.artelkioskwd100

import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.artel.artelkioskwd100.databinding.ActivityAdministracionBinding
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import android.os.Environment





class administracion : AppCompatActivity() {
    private lateinit var resolvedApplist: List<ResolveInfo>
    lateinit var mainBinding: ActivityAdministracionBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_administracion)




        mainBinding = ActivityAdministracionBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        resolvedApplist = packageManager
            .queryIntentActivities(
                Intent(Intent.ACTION_MAIN,null)
                .addCategory(Intent.CATEGORY_LAUNCHER),0)
        val appList = ArrayList<AppBlock>()

        for (ri in resolvedApplist) {
            if(ri.activityInfo.packageName!=this.packageName) {
                val app = AppBlock(
                    ri.loadLabel(packageManager).toString(),
                    ri.activityInfo.loadIcon(packageManager),
                    ri.activityInfo.packageName
                )
                appList.add(app)
            }
        }
        mainBinding.appList.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL )
        mainBinding.appList.adapter = Adapter(this).also {
            it.passAppList(appList.sortedWith(
                Comparator<AppBlock> { o1, o2 -> o1?.appName?.compareTo(o2?.appName?:"",true)?:0; }
            ))
        }
        val btnactualizar = findViewById<Button>(R.id.BTN_Actualizar)

        btnactualizar.setOnClickListener {
            Toast.makeText(this@administracion, "Actualizando Letra", Toast.LENGTH_SHORT).show()
            Update("http://172.16.240.250/AndroidVersion/letra.apk")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun Update(apkurl: String?) {
        try {
            //val file: File = File(PATH + "app.apk")
            Thread {

                val u = URL(apkurl)
                val conn: URLConnection = u.openConnection()
                val contentLength: Int = conn.getContentLength()
                val stream = DataInputStream(u.openStream())
                val buffer = ByteArray(contentLength)
                stream.readFully(buffer)
                stream.close()
                val PATH = Environment.getExternalStorageDirectory().toString() + "/download/"

                val outputFile = File(PATH + "app.apk")
                val fos = DataOutputStream(FileOutputStream(outputFile))
                fos.write(buffer)
                fos.flush()
                fos.close()
                val APKURI = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext().getPackageName().toString() + ".provider",
                    outputFile
                )
                val APKURI2 =Uri.fromFile(outputFile);
                Log.d("apkuri: ", APKURI.toString())
                val promptInstall = Intent(Intent.ACTION_VIEW)
                    .setDataAndType(
                        APKURI,
                        "application/vnd.android" + ".package-archive"
                    ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                //Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", createImageFile());
                startActivity(promptInstall)
                /*
                val url = URL(apkurl)
                val c: HttpURLConnection = url.openConnection() as HttpURLConnection
                val PATH = Environment.getStorageDirectory().toString() + "/download/"
                val file = File(PATH)
                c.setRequestMethod("GET")
                c.setDoOutput(true)
                c.connect()

                file.mkdirs()
                val outputFile = File(PATH + "app.apk")
                val fos = FileOutputStream(outputFile)
                val `is`: InputStream = c.getInputStream()
                val buffer = ByteArray(1024)
                var len1 = 0
                while (`is`.read(buffer).also { len1 = it } != -1) {
                    fos.write(buffer, 0, len1)
                }
                fos.close()
                `is`.close() //till here, it works fine - .apk is download to my sdcard in download file
                /*
                val promptInstall = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(PATH + "app.apk"))
                    .setType("application/vnd.android.package-archive")
                startActivity(promptInstall) //installation is not working


                 */

                file.setReadable(true, false)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file), "application/vnd.android.package-archive")
                startActivity(intent)

                 */
            }.start()


        } catch (e: IOException) {
            Toast.makeText(applicationContext, "Update error!", Toast.LENGTH_LONG).show()
        }
    }
}
