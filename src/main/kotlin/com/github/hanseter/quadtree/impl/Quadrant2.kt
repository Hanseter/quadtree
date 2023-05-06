package com.github.hanseter.quadtree.impl

import com.github.hanseter.quadtree.QuadtreeOptions

//This takes about twice the time for insertion compared to Quadrant
//However querying is ~10% faster
interface Quadrant2<T> {
    val minX: Double
    val minY: Double
    val maxX: Double
    val maxY: Double
    fun insert(entry: Entry<T>): Quadrant2<T>

    fun find(x: Double, y: Double, list: MutableList<T>)

    fun canContain(entry: Entry<T>) =
        this.minX <= entry.minX && this.minY <= entry.minY && this.maxX >= entry.maxX && this.maxY >= entry.maxY

    fun contains(x: Double, y: Double) =
        minX <= x && x <= maxX
                && minY <= y && y <= maxY
}