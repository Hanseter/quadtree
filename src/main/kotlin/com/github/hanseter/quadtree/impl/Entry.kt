package com.github.hanseter.quadtree.impl

data class Entry<T>(
    val minX: Double,
    val minY: Double,
    val maxX: Double,
    val maxY: Double,
    val value: T
) {
    lateinit var containingQuadrant: Quadrant<T>
    fun remove() {
        containingQuadrant.remove(this)
    }
    fun contains(x: Double, y: Double) =
        minX <= x && x <= maxX
                && minY <= y && y <= maxY

    fun intersects(
        minX: Double,
        minY: Double,
        maxX: Double,
        maxY: Double,
    ) = (this.minX <= maxX &&
            minX <= this.maxX &&
            this.minY <= maxY &&
            minY <= this.maxY)
}