package com.github.hanseter.quadtree

import net.jqwik.api.*
import net.jqwik.api.constraints.Size
import net.jqwik.engine.properties.arbitraries.EdgeCasesSupport
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.anyForType
import org.junit.jupiter.api.Test
import java.util.stream.Stream
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue


const val BOX_COUNT = 50

class QuadtreePropertyTest {


    @Test
    fun sortList() {
        val list = (0..9).map { Random.nextInt() }
        val sorted = list.sorted()


        //size has to be the same

        assertTrue { list.size == sorted.size }



        //nth element has to be smaller than (n+1)th

        sorted.zipWithNext().forEach { (a,b) ->
            assertTrue { a < b }
        }


        //all elements in the original list have to be in the sorted list

        val remaining = sorted.toMutableList()

        list.forEach {
            assertTrue { it in remaining }
            remaining.remove(it)
        }


    }

    @Property
    fun example(@ForAll nums: List<Int>) {
        println(nums)
    }


    @Property
    fun shrinkingExample(@ForAll nums: List<Int>) {
        assertTrue("Some crazy assertion") {
            nums.size < 100 || nums.sum() % 7 > 1
        }
    }


    @Property
    fun findByPoint(
        @ForAll @Size(max = BOX_COUNT) xs: List<Double>,
        @ForAll @Size(max = BOX_COUNT) ys: List<Double>,
        @ForAll @Size(max = BOX_COUNT) widths: List<Double>,
        @ForAll @Size(max = BOX_COUNT) heights: List<Double>,
        @ForAll px: Double,
        @ForAll py: Double
    ) {
        val quadtree = Quadtree<Box>()
        val boxes = xs.zip(ys, widths, heights).map { (x, y, w, h) -> Box(x, y, w.absoluteValue, h.absoluteValue) }
        boxes.forEach {
            val (x, y, w, h) = it
            quadtree.insert(x, y, x+w, y+h, it)
        }
        val qTreeResults = quadtree.find(px, py).toSet()
        val bruteForceResults = boxes.filter { it.contains(px, py) }.toSet()
        assertEquals(bruteForceResults, qTreeResults)
    }

    data class Box(val x: Double, val y: Double, val width: Double, val height: Double) {
        fun contains(x: Double, y: Double) =
            (x in this.x..(this.x + width.absoluteValue))
                    && (y in this.y..(this.y + height.absoluteValue))
    }

    data class Point(val x: Double, val y: Double)

    @Property
    fun findByPointButBetter(
        @ForAll("boxes") @Size(max = BOX_COUNT) boxes: List<Box>,
        @ForAll("points") point: Point,
    ) {
        val quadtree = Quadtree<Box>()
        boxes.forEach {
            val (x, y, w, h) = it
            quadtree.insert(x, y, x + w.absoluteValue, y + h.absoluteValue, it)
        }
        val qTreeResults = quadtree.find(point.x, point.y).toSet()
        val bruteForceResults = boxes.filter { it.contains(point.x, point.y) }.toSet()
        assertEquals(
            qTreeResults, bruteForceResults,
            "Missing: ${(bruteForceResults - qTreeResults)}\n" +
                    "Additional: ${qTreeResults - bruteForceResults}"
        )
    }

    @Provide
    fun boxes(): Arbitrary<List<Box>> = anyForType<Box>().list()

    @Provide
    fun points(): Arbitrary<Point> = anyForType<Point>()

    @Provide
    fun specificBoxes(): Arbitrary<List<Box>> {
        val xs = Int.any()
        val ys = Int.any()
        val ws = Int.any().greaterOrEqual(0)
        val hs = Int.any().greaterOrEqual(0)
        return Combinators.combine(xs, ys, ws, hs).`as` { x, y, w, h ->
            Box(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
        }.list()
    }


    private class BoxArbitrary : Arbitrary<Box> {
        override fun generator(genSize: Int): RandomGenerator<Box> {
            val xGen = Double.any().generator(genSize)
            val yGen = Double.any().generator(genSize)
            val wGen = Double.any().generator(genSize)
            val hGen = Double.any().generator(genSize)
            return RandomGenerator { random ->
                object : Shrinkable<Box> {
                    override fun value(): Box {
                        return Box(
                            xGen.next(random).value(),
                            yGen.next(random).value(),
                            wGen.next(random).value(),
                            hGen.next(random).value()
                        )
                    }

                    override fun shrink(): Stream<Shrinkable<Box>> = Stream.empty()

                    override fun distance(): ShrinkingDistance =
                        ShrinkingDistance.of(0)

                }
            }
        }

        override fun edgeCases(maxEdgeCases: Int): EdgeCases<Box> {
            return EdgeCasesSupport.combine(
                listOf(
                    Int.any().asGeneric(),
                    Int.any().asGeneric(),
                    Int.any().asGeneric(),
                    Int.any().asGeneric()
                ), { (x, y, w, h) ->
                    Box(
                        (x as Int).toDouble(),
                        (y as Int).toDouble(),
                        (w as Int).toDouble(),
                        (h as Int).toDouble()
                    )
                }, maxEdgeCases
            )
        }

    }

    private fun <T> List<T>.zip(vararg lists: List<T>): List<List<T>> {
        val minSize = lists.minOfOrNull(List<T>::size)?.coerceAtMost(size) ?: size
        val iterators = listOf(iterator()) + lists.map { it.iterator() }
        return (0 until minSize).map { _ ->
            iterators.map { it.next() }
        }
    }
}