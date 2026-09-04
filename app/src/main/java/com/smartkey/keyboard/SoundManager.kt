package com.smartkey.keyboard

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream

class SoundManager(context: Context) {
    private val ctx = context.applicationContext
    private var pool: SoundPool? = null
    private var clickId = -1

    fun init() {
        if (pool != null) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attrs)
            .build()
        val file = generateClick()
        if (file != null) {
            clickId = pool!!.load(file.absolutePath, 1)
        }
    }

    fun play() {
        val p = pool ?: return
        if (clickId < 0) return
        try {
            p.play(clickId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun release() {
        pool?.release()
        pool = null
    }

    private fun generateClick(): File? {
        return try {
            val dir = File(ctx.cacheDir, "sounds")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "click.wav")
            if (file.exists() && file.length() > 0) return file
            val sampleRate = 44100
            val length = 0.03
            val count = (sampleRate * length).toInt()
            val data = ByteArray(count * 2)
            var i = 0
            while (i < count) {
                val t = i.toDouble() / sampleRate
                val amp = Math.exp(-t * 90.0)
                val wave = Math.sin(2.0 * Math.PI * 1800.0 * t)
                val s = (amp * wave * 7000.0).toInt()
                data[i * 2] = (s and 0xff).toByte()
                data[i * 2 + 1] = ((s shr 8) and 0xff).toByte()
                i++
            }
            writeWav(file, data, sampleRate)
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun writeWav(file: File, data: ByteArray, sampleRate: Int) {
        val fs = FileOutputStream(file)
        val header = ByteArray(44)
        var pos = 0
        fun putBytes(b: ByteArray) {
            for (byte in b) header[pos++] = byte
        }
        fun putIntLE(v: Int) {
            header[pos++] = (v and 0xff).toByte()
            header[pos++] = ((v shr 8) and 0xff).toByte()
            header[pos++] = ((v shr 16) and 0xff).toByte()
            header[pos++] = ((v shr 24) and 0xff).toByte()
        }
        putBytes("RIFF".toByteArray())
        putIntLE(36 + data.size)
        putBytes("WAVE".toByteArray())
        putBytes("fmt ".toByteArray())
        putIntLE(16)
        putBytes(byteArrayOf(1, 0))
        putBytes(byteArrayOf(1, 0))
        putIntLE(sampleRate)
        putIntLE(sampleRate * 2)
        putBytes(byteArrayOf(2, 0))
        putBytes(byteArrayOf(16, 0))
        putBytes("data".toByteArray())
        putIntLE(data.size)
        fs.write(header)
        fs.write(data)
        fs.close()
    }
}