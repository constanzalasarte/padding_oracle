package org.seginf

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.experimental.xor

class PaddingOracleTest {

    private val key = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7) // Same key as in Main.kt
    private val server = Server(key) // Initialize the Server with the key
    private val paddingOracle = PaddingOracle(server) // Initialize PaddingOracle with the Server instance
    private val blockSize = 8 // Define blockSize for clarity in tests
    private val x1_all_zeros = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0) // Common x1 for these tests

    @Test
    fun `attack should correctly decrypt x2A to expected m2`() {
        // This test uses the specific x2A and expectedM2 from your problem description.
        // x2A was derived from (m2 XOR key) where x1 (IV) was all zeros.
        val x2A = byteArrayOf(2, 2, 2, 0, 6, 7, 7, 5)
        val expectedM2 = byteArrayOf(2, 3, 0, 3, 2, 2, 1, 2)

        // Pass x1_all_zeros to the attack function
        val actualM2 = paddingOracle.attack(x1_all_zeros, x2A)

        assertTrue(expectedM2.contentEquals(actualM2))
    }

    @Test
    fun `attack should work for a different known ciphertext (x2) and a non-zero x1`() {
        // Let's set a known m2 and x1, then calculate the corresponding x2.
        val knownM2 = byteArrayOf(10, 20, 30, 40, 50, 60, 70, 80) // Example m2
        val knownX1 = byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1) // Example x1 (non-zero)

        // To get testX2:
        // We know m2 = D_K(x2) XOR x1
        // So, D_K(x2) = m2 XOR x1
        val expectedDX2 = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            expectedDX2[i] = (knownM2[i] xor knownX1[i])
        }

        // And from the server's decryptBlock: D_K(x2) = x2 XOR key
        // So, x2 = D_K(x2) XOR key
        val testX2 = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            testX2[i] = (expectedDX2[i] xor key[i])
        }

        // The attack should recover the original knownM2
        val actualM2 = paddingOracle.attack(knownX1, testX2)

        assertTrue(knownM2.contentEquals(actualM2))
    }

    @Test
    fun `attack should work for a block with all zero content and padding 8`() {
        // This tests a scenario where D_K(x2) evaluates to a block that,
        // when XORed with a chosen 'r' in the attack, yields valid padding.
        // If we target m2 = [0,0,0,0,0,0,0,0] and x1 = [0,0,0,0,0,0,0,0]
        // then D_K(x2) should also be [0,0,0,0,0,0,0,0].
        // So, x2 = [0,0,0,0,0,0,0,0] XOR key (since D_K(x2) = x2 XOR key)
        val targetM2 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val x1ForThisTest = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

        // Calculate D_K(x2) = targetM2 XOR x1ForThisTest = [0,0,0,0,0,0,0,0]
        val expectedDX2 = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            expectedDX2[i] = (targetM2[i] xor x1ForThisTest[i])
        }

        // Calculate the corresponding x2 that would produce this D_K(x2)
        val testX2 = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            testX2[i] = (expectedDX2[i] xor key[i])
        }

        val actualM2 = paddingOracle.attack(x1ForThisTest, testX2)
        assertTrue(targetM2.contentEquals(actualM2))
    }
}