/**
 * Copyright (c) 2019 addcn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addcn.im.request

import com.addcn.im.interfaces.OnProgressListener
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Author:WangYongQi
 * upload image
 */

object UploadImage {

    private val BOUNDARY = java.util.UUID.randomUUID().toString()

    fun uploadImage(url: String, token: String, file: File, resultListener: OnProgressListener?) {
        val params = HashMap<String, String>()
        params["token"] = token
        params["picture"] = file.path
        val files = HashMap<String, File>()
        files["picture"] = file
        uploadImage(url, params, files, resultListener)
    }

    private fun uploadImage(url: String, params: Map<String, String>, files: Map<String, File>?, resultListener: OnProgressListener?) {
        Thread(Runnable {
            var result = ""
            try {
                val prefix = "--"
                val linEnd = "\r\n"
                val uri = URL(url)
                val connection = uri.openConnection() as HttpURLConnection
                connection.readTimeout = 300000
                connection.connectTimeout = 300000
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Charset", "UTF-8")
                connection.setRequestProperty("connection", "keep-alive")
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$BOUNDARY")
                val stringBuilder = StringBuilder()
                for ((key, value) in params) {
                    stringBuilder.append(prefix)
                    stringBuilder.append(BOUNDARY)
                    stringBuilder.append(linEnd)
                    stringBuilder.append("Content-Disposition: form-data; name=\"$key\"$linEnd")
                    stringBuilder.append("Content-Type: text/plain; charset=UTF-8$linEnd")
                    stringBuilder.append("Content-Transfer-Encoding: 8bit$linEnd")
                    stringBuilder.append(linEnd)
                    stringBuilder.append(value)
                    stringBuilder.append(linEnd)
                }
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.write(stringBuilder.toString().toByteArray())

                if (files != null)
                    for ((_, value) in files) {
                        val sb = StringBuilder()
                        sb.append(prefix)
                        sb.append(BOUNDARY)
                        sb.append(linEnd)
                        sb.append("Content-Disposition: form-data; name=\"picture\"; filename=\"" + value.name + "\"" + linEnd)
                        sb.append("Content-Type: application/octet-stream; charset=UTF-8$linEnd")
                        sb.append(linEnd)
                        outputStream.write(sb.toString().toByteArray())
                        val inputStream = FileInputStream(value)
                        var len: Int
                        val buffer = ByteArray(1024)
                        do {
                            len = inputStream.read(buffer)
                            if (len != -1) {
                                outputStream.write(buffer, 0, len)
                            }
                        } while (len != -1)
                        inputStream.close()
                        outputStream.write(linEnd.toByteArray())
                    }
                val endData = (prefix + BOUNDARY + prefix + linEnd).toByteArray()
                outputStream.write(endData)
                outputStream.flush()
                val res = connection.responseCode
                val sb = StringBuilder()
                if (res == 200) {
                    var ch: Int
                    val inputStream = connection.inputStream
                    do {
                        ch = inputStream.read()
                        if (ch != -1) {
                            sb.append(ch.toChar())
                        }
                    } while (ch != -1)
                    inputStream.close()
                }
                outputStream.close()
                connection.disconnect()
                result = sb.toString()
            } catch (ex: Exception) {
            }
            resultListener?.onResult(result)
        }).start()
    }

}
