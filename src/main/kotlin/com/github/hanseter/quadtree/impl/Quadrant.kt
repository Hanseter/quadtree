package com.github.hanseter.quadtree.impl

import com.github.hanseter.quadtree.QuadtreeOptions

class Quadrant<T>(
    private val minX: Double,
    private val minY: Double,
    private val maxX: Double,
    private val maxY: Double,
    private val options: QuadtreeOptions
) {

    private val entries = ArrayList<Entry<T>>()

    private var topLeft: Quadrant<T>? = null
    private var topRight: Quadrant<T>? = null
    private var bottomRight: Quadrant<T>? = null
    private var bottomLeft: Quadrant<T>? = null


    fun insert(minX: Double, minY: Double, maxX: Double, maxY: Double, value: T) {
        insert(Entry(minX, minY, maxX, maxY, value))
    }

    private fun insert(entry: Entry<T>): Boolean {
        if (!canContain(entry)) return false
        if (topLeft != null) {
            if (!topLeft!!.insert(entry)
                && !topRight!!.insert(entry)
                && !bottomRight!!.insert(entry)
                && !bottomLeft!!.insert(entry)
            ) {
                entries.add(entry)
            }
            return true
        }
        entries.add(entry)
        if (entries.size > options.maxElemsPerQuadrant) {
            splitQuadrant()
        }
        return true
    }

    private fun splitQuadrant() {
        val midX = minX / 2 + maxX / 2
        val midY = minY / 2 + maxY / 2
        topLeft = Quadrant(minX, minY, midX, midY, options)
        topRight = Quadrant(midX, minY, maxX, midY, options)
        bottomRight = Quadrant(midX, midY, maxX, maxY, options)
        bottomLeft = Quadrant(minX, midY, midX, maxY, options)
        val tmp = entries.toList()
        entries.clear()
        tmp.forEach {
            insert(it)
        }
    }

    private fun canContain(entry: Entry<T>) =
        this.minX <= entry.minX && this.minY <= entry.minY && this.maxX >= entry.maxX && this.maxY >= entry.maxY

    private fun canContain(
        minX: Double,
        minY: Double,
        maxX: Double,
        maxY: Double,
    ) = this.minX <= minX && this.minY <= minY && this.maxX >= maxX && this.maxY >= maxY

    private fun contains(x: Double, y: Double) =
        minX <= x && x <= maxX
                && minY <= y && y <= maxY

    fun find(x: Double, y: Double, list: MutableList<T>) {
        if (!contains(x, y)) return
        entries.forEach {
            if (it.contains(x, y)) {
                list += it.value
            }
        }
        if (topLeft == null) return
        topLeft!!.find(x, y, list)
        topRight!!.find(x, y, list)
        bottomRight!!.find(x, y, list)
        bottomLeft!!.find(x, y, list)
    }

    fun find(minX: Double, minY: Double, maxX: Double, maxY: Double, list: MutableList<T>) {
        if (!canContain(minX, minY, maxX, maxY)) return
        entries.forEach {
            if (it.intersects(minX, minY, maxX, maxY)) {
                list += it.value
            }
        }
        if (topLeft == null) return
        topLeft!!.find(minX, minY, maxX, maxY, list)
        topRight!!.find(minX, minY, maxX, maxY, list)
        bottomRight!!.find(minX, minY, maxX, maxY, list)
        bottomLeft!!.find(minX, minY, maxX, maxY, list)
    }
}