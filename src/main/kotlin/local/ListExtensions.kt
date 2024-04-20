package org.example.local

fun <T> MutableList<T>.nextOf(index: Int): T {
    return this[(index + 1).mod(this.size)]
}

fun <T> MutableList<T>.prevOf(index: Int): T {
    return this[(index - 1).mod(this.size)]
}
