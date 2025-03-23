package uk.matvey.slon.random

import kotlin.random.Random

private val ALPHA = ('a'..'z').toList()
private val NUMERIC = ('0'..'9').toList()
private val ALPHANUMERIC = ALPHA + NUMERIC

fun randomWord(
    length: Int = Random.nextInt(1, 32),
) = buildString {
    repeat(length) {
        append(ALPHANUMERIC.random())
    }
}

fun randomSentence(length: Int = Random.nextInt(1, 16)) = (0..<length).joinToString(" ") {
    randomWord()
}

fun randomParagraph(length: Int = Random.nextInt(1, 8)) = (0..<length).joinToString(". ") {
    randomSentence()
}