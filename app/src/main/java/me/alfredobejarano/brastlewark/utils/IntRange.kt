package me.alfredobejarano.brastlewark.utils

fun IntRange.has(value: Number) = value.toInt() in this
fun <T> List<T>.getRangeFrom(
    sorterBlock: (element: T) -> Int,
    rangeBlock: (sortResult: Pair<T, T>) -> IntRange
): IntRange {
    val sort = this.sortedBy { sorterBlock(it) }
    return rangeBlock(sort.first() to sort.last())
}