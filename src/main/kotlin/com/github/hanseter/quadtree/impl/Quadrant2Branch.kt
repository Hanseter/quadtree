package com.github.hanseter.quadtree.impl

import com.github.hanseter.quadtree.QuadtreeOptions

class Quadrant2Branch<T>(
    override val minX: Double,
    override val minY: Double,
    override val maxX: Double,
    override val maxY: Double,
    private val options: QuadtreeOptions
) : Quadrant2<T> {

    private val entries = ArrayList<Entry<T>>()


    private var topLeft: Quadrant2<T>
    private var topRight: Quadrant2<T>
    private var bottomRight: Quadrant2<T>
    private var bottomLeft: Quadrant2<T>

    init {
        val midX = minX / 2 + maxX / 2
        val midY = minY / 2 + maxY / 2
        topLeft = Quadrant2Leaf<T>(minX, minY, midX, midY, options)
        topRight = Quadrant2Leaf(midX, minY, maxX, midY, options)
        bottomRight = Quadrant2Leaf(midX, midY, maxX, maxY, options)
        bottomLeft = Quadrant2Leaf(minX, midY, midX, maxY, options)
    }

    override fun insert(entry: Entry<T>): Quadrant2<T> {
        if (topLeft.canContain(entry)) topLeft = topLeft.insert(entry)
        else if (topRight.canContain(entry)) topRight = topRight.insert(entry)
        else if (bottomRight.canContain(entry)) bottomRight = bottomRight.insert(entry)
        else if (bottomLeft.canContain(entry)) bottomLeft = bottomLeft.insert(entry)
        else entries.add(entry)
        return this
    }

    override fun find(x: Double, y: Double, list: MutableList<T>) {
        if (!contains(x, y)) return
        entries.forEach {
            if (it.contains(x, y)) {
                list += it.value
            }
        }
        topLeft.find(x, y, list)
        topRight.find(x, y, list)
        bottomRight.find(x, y, list)
        bottomLeft.find(x, y, list)
    }
}