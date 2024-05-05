package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Entry
import com.github.hanseter.quadtree.impl.Quadrant

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
     * A copied list of all entries in this tree.
     */
    val values: List<T>
        get() = entries.keys.toList()

    private val entries = HashMap<T, Entry<T>>()

    /**
     * Insert a new element into the quadtree.
     * Calling this method with an [value] that is already in the quadtree, it will merely update its coordinates.
     */
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

    /**
     * Finds all elements at the provided point.
     */
    fun find(x: Double, y: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(x, y, ret)
        return ret
    }

    /**
     * Finds all elements intersecting with the provided rectangle.
     */
    fun find(minX: Double, minY: Double, maxX: Double, maxY: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(minX, minY, maxX, maxY, ret)
        return ret
    }

    /**
     * Removes an entry from the quadtree.
     */
    fun remove(toRemove: T) {
        entries.remove(toRemove)?.remove()
    }

    /**
     * Removes all the provided entries from the quadtree
     * @return true if any of the specified elements was removed from the collection, false if the collection was not modified.
     */
    fun removeAll(toRemove: Collection<T>) {
        toRemove.forEach {
            remove(it)
        }
    }

    /**
     * Clears the quadtree, removing all entries, leaving its internal structure as is.
     */
    fun clear() {
        entries.values.forEach { it.remove() }
        entries.clear()
    }
}