package com.github.hanseter.quadtree

data class QuadtreeOptions(
    val maxElemsPerQuadrant :Int = 100,
    val initialX: Double= -100_000.0,
    val initialY: Double= -100_000.0,
    val initialSize: Double= 200_000.0
)