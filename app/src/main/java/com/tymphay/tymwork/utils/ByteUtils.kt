package com.tymphay.tymwork.utils

import java.util.*

object ByteUtils {

    fun hexStringToBytes(hexString: String): ByteArray {
        val hexString = hexString.uppercase(Locale.getDefault())
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val byteArrayResult = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            byteArrayResult[i] = (charToByte(hexChars[pos]).toInt().shl(4) or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return byteArrayResult
    }

    fun bytesToHexString(src: ByteArray): String {
        val stringBuilder = StringBuilder("")
        for (i in src.indices) {
            val v = (src[i].toInt() and 0xFF)
            val hv = Integer.toHexString(v)
            stringBuilder.append(if (hv.length < 2) 0 else hv)
        }
        return stringBuilder.toString()
    }

    private fun charToByte(c: Char): Byte = "0123456789ABCDEF".indexOf(c).toByte()

}
