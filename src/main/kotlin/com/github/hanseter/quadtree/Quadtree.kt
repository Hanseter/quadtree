package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Entry
import com.github.hanseter.quadtree.impl.Quadrant
import java.util.IdentityHashMap

/**
 * A quadtree for optimized spatial lookups.
 * The quadtree can dynamically add and remove elements, however each element can only be added once to the quadtree.
 * Adding the same instance multiple times to the tree will merely update its associated rectangle.
 * While it is a good idea to use a sensible initial size for the root node of the tree, it will also automatically grow,
 * if you insert an element that would not fit into the tree otherwise.
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

    /**
     * A list of all entries in this tree.
     */
    val values: List<T>
        get() = entries.keys.toList()

    private val entries = IdentityHashMap<T, Entry<T>>()

    fun insert(minX: Double, minY: Double, maxX: Double, maxY: Double, value: T) {
        val entry = Entry(minX, minY, maxX, maxY, value)
        insert(entry)
        entries.put(value, entry)?.remove()
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

    fun find(minX: Double, minY: Double, maxX: Double, maxY: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(minX, minY, maxX, maxY, ret)
        return ret
    }

    /**
     * Removes an entry from the quadtree. The element to remove has to be the same instance that was inserted.
     */
    fun remove(toRemove: T) {
        entries.remove(toRemove)?.remove()
    }
}