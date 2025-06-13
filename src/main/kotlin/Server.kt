package org.seginf

import kotlin.experimental.xor

class Server(private val key: ByteArray) {
    private val blockSize: Int = 8 // bytes

    private fun decryptBlock(x1: ByteArray): ByteArray {
        val decryptedBlock = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            decryptedBlock[i] = (x1[i] xor key[i])
        }
        return decryptedBlock
    }

    fun isPaddingValid(x1: ByteArray, x2: ByteArray): Boolean {
        val p2 = decryptBlock(x2)

        val result = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            result[i] = (p2[i] xor x1[i])
        }

        // Verify that the last bit of the decrypted block is equal to the number of padding bits
        // and that all the padding bits are equal to the number of padding bits
        val lastBitValue = result[blockSize - 1].toUByte().toInt()

        // Check that the lastBitValue is between 1 and blockSize
        if (lastBitValue !in 1..blockSize) {
            return false
        }

        // Check that the last lastBitValue bits are equal to the lastBitValue
        for (i in 1..lastBitValue) {
            if (result[blockSize - i].toUByte().toInt() != lastBitValue) {
                return false
            }
        }

        return true
    }
}