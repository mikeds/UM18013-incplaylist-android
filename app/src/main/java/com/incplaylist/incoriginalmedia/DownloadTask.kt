package com.incplaylist.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*

@Suppress("DEPRECATION")
class DownloadTask(
        @SuppressLint("StaticFieldLeak")
        val context: Context,
        val yourUrl: String,
        val fileName: String,
        val position: Int,
        val listener: DownloadListener) : AsyncTask<Boolean, Void, Boolean>()
{
    override fun doInBackground(vararg booleans: Boolean?): Boolean {

        var lenghtOfFile: Long = 0
        var status = 0
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(yourUrl)
                .build()
        var response: Response? = null


        try {
            response = client.newCall(request).execute()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        lenghtOfFile = response!!.body()!!.contentLength()
        val inputStream: InputStream

        try {
            assert(response!!.body() != null)
            inputStream = response!!.body()!!.byteStream()

            val buff = ByteArray(1024 * 4)
            var downloaded: Long = 0


            val folder = File(context.filesDir, "elpaboritos")
            if (!folder.exists()) {
                folder.mkdir()
            }

            val documentFile = File("$folder/$fileName")
            documentFile.parentFile.mkdirs()
            try {
                documentFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var output: OutputStream? = null
            try {
                output = FileOutputStream(documentFile, false)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            while (true) {

                val readed = inputStream.read(buff)

                if (readed == -1) {
                    break
                }
                if (isCancelled) {
                    break
                }
                downloaded += readed.toLong()
                status = (downloaded * 100 / lenghtOfFile).toInt()

                listener.downloadProgress(status)

                output!!.write(buff, 0, readed)

            }

            output!!.flush()
            output.close()
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }


    override fun onPreExecute() {
        super.onPreExecute()
    }


    override fun onProgressUpdate(vararg values: Void) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(objects: Boolean?) {
        super.onPostExecute(objects)
        if (objects!!) {
            listener.onDownloadComplete(true, position)
        } else {
            listener.onDownloadComplete(false, position)
        }
    }

    override fun onCancelled(aBoolean: Boolean?) {
        super.onCancelled(aBoolean)
        Toast.makeText(context, "Download Cancelled", Toast.LENGTH_SHORT).show()
        val folder = File(Environment.getDownloadCacheDirectory().toString() + "/elpaboritos/")
        val documentFile = File("$folder/$fileName")
        documentFile.delete()
    }



    interface DownloadListener {

        fun onDownloadComplete(download: Boolean, pos: Int)
        fun downloadProgress(status: Int)
    }
}