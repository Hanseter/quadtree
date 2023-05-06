package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Quadrant

class Quadtree<T>(
    val options: QuadtreeOptions = QuadtreeOptions()
) {

    private val root = Quadrant<T>(
        -100000.0,
        -100000.0,
        100000.0,
        100000.0,
        options
    )

    fun insert(minX: Double, minY: Double, maxX: Double, maxY: Double, value: T) {
        root.insert(minX, minY, maxX, maxY, value)
    }

    fun find(x: Double, y: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(x, y, ret)
        return ret
    }

    fun find(minX: Double, minY: Double, maxX: Double, maxY: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(minX, minY, maxX, maxY, ret)
        return ret
    }

    /**
     * Removes an entry from the quadtree. The element to remove has to be the same instance that was inserted.
     */
    fun remove(toRemove: T) {

    }
}