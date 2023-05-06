package com.github.hanseter.quadtree

data class QuadtreeOptions(
    val maxDepth : Int = Int.MAX_VALUE,
    val maxElemsPerQuadrant :Int = 100,
    val minQuadrantSize: Double = 100.0,
    val initialX: Double= -100_000.0,
    val initialY: Double= -100_000.0,
    val initialSize: Double= 200_000.0
)