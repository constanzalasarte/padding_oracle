package org.seginf

fun main() {
    val key = generateKey()
    val server = Server(key)
    val x1 = generateX1()
    val x2 = generateX2(false)
    println("Tiene un relleno valido? " + server.isPaddingValid(x1, x2))
    val x2T = generateX2(true)
    println("Tiene un relleno valido? " + server.isPaddingValid(x1, x2T))

    val x2A = generateX2Attack()
    val paddingOracle = PaddingOracle(server)
    val m2 = paddingOracle.attack(x1, x2A)
    println(m2.contentEquals(generateByteArray(intArrayOf(2,3,0,3,2,2,1,2))))
}

private fun generateKey() : ByteArray {
    return generateByteArray(intArrayOf(0,1,2,3,4,5,6,7))
}

private fun generateX1() : ByteArray {
    return generateByteArray(intArrayOf(0,0,0,0,0,0,0,0))
}
private fun generateX2(valid: Boolean) : ByteArray {
    if(valid) return generateByteArray(intArrayOf(0,1,2,3,4,6,5,4))
    return generateByteArray(intArrayOf(1,1,1,1,1,1,1,1))
}

private fun generateX2Attack() : ByteArray {
    return generateByteArray(intArrayOf(2,2,2,0,6,7,7,5))
}

private fun generateByteArray(a: IntArray): ByteArray {
    val key = ByteArray(8)
    for(i in 0..7){
        key[i] = a[i].toByte()
    }
    return key
}