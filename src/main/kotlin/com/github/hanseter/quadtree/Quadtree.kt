package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Entry
import com.github.hanseter.quadtree.impl.Quadrant

/**
 * A quadtree for optimized spatial lookups.
 */
class Quadtree<T>(
    val options: QuadtreeOptions = QuadtreeOptions()
) {

    private var root = Quadrant<T>(
        options.initialX,
        options.initialY,
        options.initialX + options.initialSize,
        options.initialY + options.initialSize,
        options
    )

    fun insert(minX: Double, minY: Double, maxX: Double, maxY: Double, value: T) {
        insert(Entry(minX, minY, maxX, maxY, value))
    }

    private tailrec fun insert(entry: Entry<T>) {
        if (root.insert(entry)) return
        root = root.createLargerQuadrant(entry)
        insert(entry)
    }

    fun find(x: Double, y: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(x, y, ret)
        return ret
    }

    fun find2(x: Double, y: Double): List<T> {
        val ret = ArrayList<T>()
        root.find2(x, y, ret)
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