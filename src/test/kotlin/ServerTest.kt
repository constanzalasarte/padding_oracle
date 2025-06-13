package org.seginf

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.experimental.xor

class ServerTest {

    private val key = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7)
    private val server = Server(key)
    private val blockSize = 8

    private fun generateResultForPaddingTest(paddingValue: Int): ByteArray {
        val arr = ByteArray(blockSize)
        for (i in 0 until blockSize - paddingValue) {
            arr[i] = 0
        }
        for (i in 0 until paddingValue) {
            arr[blockSize - 1 - i] = paddingValue.toByte()
        }
        return arr
    }

    private fun generateX2ForResult(targetResult: ByteArray, x1: ByteArray): ByteArray {
        val x2 = ByteArray(blockSize)
        for (i in 0 until blockSize) {
            x2[i] = (targetResult[i] xor x1[i] xor key[i])
        }
        return x2
    }

    @Test
    fun `isPaddingValid should return true for valid padding of 1`() {
        val x1 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val targetResult = generateResultForPaddingTest(1)
        val x2 = generateX2ForResult(targetResult, x1)
        assertTrue(server.isPaddingValid(x1, x2))
    }

    @Test
    fun `isPaddingValid should return true for valid padding of 8`() {
        val x1 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val targetResult = generateResultForPaddingTest(8)
        val x2 = generateX2ForResult(targetResult, x1)
        assertTrue(server.isPaddingValid(x1, x2))
    }

    @Test
    fun `isPaddingValid should return true for valid padding of 4`() {
        val x1 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val targetResult = generateResultForPaddingTest(4)
        val x2 = generateX2ForResult(targetResult, x1)
        assertTrue(server.isPaddingValid(x1, x2))
    }

    @Test
    fun `isPaddingValid should return false for invalid padding value (out of range)`() {
        val x1 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val targetResultTooLarge = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 9)
        val x2TooLarge = generateX2ForResult(targetResultTooLarge, x1)
        assertFalse(server.isPaddingValid(x1, x2TooLarge))

        val targetResultZero = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val x2Zero = generateX2ForResult(targetResultZero, x1)
        assertFalse(server.isPaddingValid(x1, x2Zero))
    }

    @Test
    fun `isPaddingValid should return false for malformed padding (not all bytes match value)`() {
        val x1 = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        val malformedResult = byteArrayOf(0, 0, 0, 0, 0, 0, 4, 4)
        val x2Malformed = generateX2ForResult(malformedResult, x1)
        assertFalse(server.isPaddingValid(x1, x2Malformed))
    }
}