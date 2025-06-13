package org.seginf

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.experimental.xor

class PaddingOracleTest {

    private val key = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7) // Same key as in Main.kt
    private val server = Server(key)
    private val paddingOracle = PaddingOracle(server)

    @Test
    fun `attack should correctly decrypt x2A to expected m2`() {
        val x2A = byteArrayOf(2, 2, 2, 0, 6, 7, 7, 5)
        val expectedM2 = byteArrayOf(2, 3, 0, 3, 2, 2, 1, 2)

        val actualM2 = paddingOracle.attack(x2A)

        assertTrue(expectedM2.contentEquals(actualM2))
    }

    @Test
    fun `attack should work for a different known ciphertext (x2)`() {
        val targetDecryptedBlock = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 1)
        val testX2 = ByteArray(8)
        for (i in 0 until 8) {
            testX2[i] = (targetDecryptedBlock[i] xor key[i])
        }

        val expectedDX2 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 1)

        val actualDX2 = paddingOracle.attack(testX2)

        assertTrue(expectedDX2.contentEquals(actualDX2))
    }
}