package com.github.hanseter.quadtree.impl

import com.github.hanseter.quadtree.QuadtreeOptions

class Quadrant2Leaf<T>(
    override val minX: Double,
    override val minY: Double,
    override val maxX: Double,
    override val maxY: Double,
    private val options: QuadtreeOptions
) : Quadrant2<T>{
    private val entries = ArrayList<Entry<T>>()

    override fun insert(entry: Entry<T>): Quadrant2<T> {
        entries.add(entry)
        if (entries.size <= options.maxElemsPerQuadrant) {
            return this
        }
        return Quadrant2Branch<T>(minX, minY, maxX, maxY, options).let {
            entries.fold(it as Quadrant2<T>) {acc, entry -> acc.insert(entry)}
        }
    }

    override fun find(x: Double, y: Double, list: MutableList<T>) {
        if (!contains(x, y)) return
        entries.forEach {
            if (it.contains(x, y)) {
                list += it.value
            }
        }
    }
}