/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package rfa.pk.rtk.dmaudiostreamer.network


import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.text.TextUtils

import com.google.gson.JsonSyntaxException

import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import javax.net.ssl.HttpsURLConnection

import dm.audiostreamer.MediaMetaData

object MusicBrowser {

    private val music = "https://firebasestorage.googleapis.com/v0/b/dmaudiostreamer.appspot.com/o/music.json?alt=media&token=64ac05a8-2f23-4cef-b25c-b488519b0650"

    var RESPONSE_CODE_SUCCESS = "200"
    var RESPONSE_CODE_CONNECTION_TIMEOUT = "9001"
    var RESPONSE_CODE_SOCKET_TIMEOUT = "903"

    // always check HTTP response code first
    // Get Response
    val dataResponse: Array<String>
        get() {
            val result = arrayOf("", "")
            try {
                val url = URL(music)
                val urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connectTimeout = 20000
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                if (Build.VERSION.SDK_INT > 13) {
                    urlConnection.setRequestProperty("Connection", "close")
                }

                urlConnection.useCaches = false
                urlConnection.doInput = true
                urlConnection.doOutput = true
                val responseCode = urlConnection.responseCode
                result[0] = responseCode.toString() + ""

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val `is` = urlConnection.inputStream
                    val rd = BufferedReader(InputStreamReader(`is`))
                    var line: String
                    val response = StringBuffer()
                    while ((line = rd.readLine()) != null) {
                        response.append(line)
                        response.append('\r')
                    }
                    rd.close()

                    if (!TextUtils.isEmpty(response)) {
                        result[0] = RESPONSE_CODE_SUCCESS
                        result[1] = response.toString()
                    }
                }

            } catch (e: UnsupportedEncodingException) {
                result[0] = RESPONSE_CODE_CONNECTION_TIMEOUT
                e.printStackTrace()
            } catch (e: ConnectTimeoutException) {
                result[0] = RESPONSE_CODE_CONNECTION_TIMEOUT
                e.printStackTrace()
            } catch (e: IOException) {
                result[0] = RESPONSE_CODE_CONNECTION_TIMEOUT
                e.printStackTrace()
            } catch (e: Exception) {
                result[0] = RESPONSE_CODE_CONNECTION_TIMEOUT
                e.printStackTrace()
            }

            return result
        }

    fun loadMusic(context: Context, loaderListener: MusicLoaderListener?) {

        val loadTask = object : AsyncTask<Void, Void, Void>() {
            internal var resp = arrayOf("", "")
            internal var listMusic: List<MediaMetaData>? = ArrayList()

            override fun doInBackground(vararg voids: Void): Void? {
                //resp = getDataResponse();
                val response = loadJSONFromAsset(context)
                listMusic = getMusicList(response, "music")
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)

                if (loaderListener != null && listMusic != null && listMusic!!.size >= 1) {
                    loaderListener.onLoadSuccess(listMusic)
                } else {
                    loaderListener!!.onLoadFailed()
                }
            }
        }
        loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun loadJSONFromAsset(context: Context): String? {
        var json: String? = null
        try {
            val `is` = context.assets.open("music.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, "UTF-8")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    fun getMusicList(response: String?, name: String): List<MediaMetaData> {
        val listArticle = ArrayList<MediaMetaData>()
        try {
            val array = JSONObject(response).getJSONArray(name)
            for (i in 0 until array.length()) {
                val infoData = MediaMetaData()
                val musicObj = array.getJSONObject(i)
                infoData.mediaId = musicObj.optString("id")
                infoData.mediaUrl = musicObj.optString("site") + musicObj.optString("source")
                infoData.mediaTitle = musicObj.optString("title")
                infoData.mediaArtist = musicObj.optString("artist")
                infoData.mediaAlbum = musicObj.optString("album")
                infoData.mediaComposer = musicObj.optString("")
                infoData.mediaDuration = musicObj.optString("duration")
                infoData.mediaArt = musicObj.optString("site") + musicObj.optString("image")
                listArticle.add(infoData)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }

        return listArticle
    }
}
