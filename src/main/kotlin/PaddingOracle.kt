package org.seginf

import kotlin.experimental.xor

class PaddingOracle(private val server: Server) {
    fun attack(x2: ByteArray): ByteArray {
        val blockSize = 8
        val dX2 = ByteArray(blockSize)
        val r = ByteArray(blockSize)

        // Iterate from the last byte to the first
        for (padding_value in 1..blockSize) {
            val byte_index_to_discover = blockSize - padding_value

            // Step 1: Prepare the bytes to the right of byte_index_to_discover
            for (j in (byte_index_to_discover + 1) until blockSize) {
                // dX2[j] has already been discovered in previous iterations.
                r[j] = (dX2[j] xor padding_value.toByte())
            }

            // Step 2: Iterate through all possible values for the current byte
            for (guess_byte in 0..255) {
                r[byte_index_to_discover] = guess_byte.toByte()

                if (server.isPaddingValid(r, x2)) {
                    // If padding is valid, we found the correct 'guess_byte'
                    dX2[byte_index_to_discover] = (guess_byte.toByte() xor padding_value.toByte())
                    break
                }
            }
        }
        return dX2
    }
}