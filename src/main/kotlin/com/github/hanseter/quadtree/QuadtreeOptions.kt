package com.github.hanseter.quadtree

/**
 * Options for the quadtree, like elements per quadrant and size of the initial quadrant.
 */
data class QuadtreeOptions(
        /**
         * The maximum number of elements than can be contained in a quadrant before it will be split.
         */
        val maxElemsPerQuadrant: Int = 100,
        /**
         * The minimum x of the initial quadrant.
         */
        val initialX: Double = -100_000.0,
        /**
         * The minimum y of the initial quadrant.
         */
        val initialY: Double = -100_000.0,
        /**
         * The size initial quadrant.
         */
        val initialSize: Double = 200_000.0
) {
    companion object {
        const val ELEMENTS_PER_QUADRANT = 100
        const val DEFAULT_INITIAL_X = -100_000.0
        const val DEFAULT_INITIAL_Y = -100_000.0
        const val DEFAULT_INITIAL_SIZE = -100_000.0
    }
}