package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Entry
import com.github.hanseter.quadtree.impl.Quadrant
import com.github.hanseter.quadtree.impl.Quadrant2
import com.github.hanseter.quadtree.impl.Quadrant2Leaf

class Quadtree2<T>(
    val options: QuadtreeOptions = QuadtreeOptions()
) {

    private var root: Quadrant2<T> = Quadrant2Leaf<T>(
        -100000.0,
        -100000.0,
        100000.0,
        100000.0,
        options
    )

    fun insert(minX: Double, minY: Double, maxX: Double, maxY: Double, value: T) {
        root = root.insert(Entry(minX, minY, maxX, maxY, value))
    }

    fun find(x: Double, y: Double): List<T> {
        val ret = ArrayList<T>()
        root.find(x, y, ret)
        return ret
    }
}