package com.github.hanseter.quadtree

data class QuadtreeOptions(
    val maxDepth : Int = Int.MAX_VALUE,
    val maxElemsPerQuadrant :Int = 100,
    val minQuadrantSize: Double = 100.0
)