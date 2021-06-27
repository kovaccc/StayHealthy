package com.example.stayhealthy.common.extensions

fun String.capitalizeAllFirst(): String { // to avoid case sensitive
    val array = this.toCharArray()

    // Uppercase first letter.
    array[0] = Character.toUpperCase(array[0])

    // Uppercase all letters that follow a whitespace character.
    for (i in 1 until array.size) {
        if (Character.isWhitespace(array[i - 1])) {
            array[i] = Character.toUpperCase(array[i])
        } else {
            array[i] = Character.toLowerCase(array[i])
        }
    }
    return String(array)
}