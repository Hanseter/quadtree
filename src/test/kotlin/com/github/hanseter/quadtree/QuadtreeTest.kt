package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Entry
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

class QuadtreeTest {

    @Test
    fun massRandomData() {
        val qTree = Quadtree<String>()
        val entries = (0..8999).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            val w = Random.nextDouble(100.0, 300.0)
            val h = Random.nextDouble(100.0, 300.0)
            Entry(x, y, x + w, y + h, UUID.randomUUID().toString())
        }
        entries.forEach { (minX, minY, maxX, maxY, value) ->
            qTree.insert(minX, minY, maxX, maxY, value)
        }
        val points = (0..999).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            x to y
        }
        points.forEach { (x, y) ->
            assertTrue {
                qTree.find(x, y).sorted() == entries.filter { it.contains(x, y) }.map { it.value }.sorted()
            }
            assertTrue {
                qTree.find(x, y).sorted() == qTree.find2(x, y).sorted()
            }
        }

        val boxes = (0..200).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            val w = Random.nextDouble(100.0, 300.0)
            val h = Random.nextDouble(100.0, 300.0)
            listOf(x, y, x + w, y + h)
        }
        boxes.forEach { (x, y, a, b) ->
            assertTrue {
                qTree.find(x, y, a, b).sorted() == entries.filter { it.intersects(x, y, a, b) }.map { it.value }
                    .sorted()
            }

        }
    }

    @Test
    fun findByBBox() {
        val qTree = Quadtree<String>(
            QuadtreeOptions(
                maxElemsPerQuadrant = 1,
                initialX = -100.0,
                initialY = -100.0,
                initialSize = 200.0
            )
        )
        qTree.insert(-80.0, 20.0, 80.0, 80.0, "Foo")
        qTree.insert(20.0, 20.0, 80.0, 75.0, "Bar")
        qTree.insert(-80.0, -80.0, -20.0, -20.0, "Baz")
        assertTrue {
            qTree.find(70.0, 80.1, 90.0, 99.0).isEmpty()
        }
        assertTrue {
            qTree.find(70.0, 79.9, 90.0, 99.0).sorted() == listOf("Foo")
        }
        assertTrue {
            qTree.find(70.0, 74.9, 90.0, 99.0).sorted() == listOf("Bar", "Foo")
        }
        assertTrue {
            qTree.find(-30.0, 74.9, 30.0, 99.0).sorted() == listOf("Bar", "Foo")
        }
        assertTrue {
            qTree.find(-200.0, -200.0, 200.0, 200.0).sorted() == listOf("Bar", "Baz", "Foo")
        }

    }

    @Test
    fun removesOnlySameInstances() {
        val qTree = Quadtree<String>()
        qTree.insert(100.0, 100.0, 250.0, 146.0, "Foo")
        assertTrue { qTree.find(150.0, 120.0).single() == "Foo" }
        qTree.remove("Foobar".take(3))
        assertTrue { qTree.find(150.0, 120.0).single() == "Foo" }
        qTree.remove("Foo")
        assertTrue { qTree.find(150.0, 120.0).isEmpty() }
    }

    @Test
    fun movesEntryIfSameInstanceIsInserted() {
        val qTree = Quadtree<String>()
        qTree.insert(100.0, 100.0, 250.0, 146.0, "Foo")
        assertTrue { qTree.find(150.0, 120.0).single() == "Foo" }
        qTree.insert(200.0, 200.0, 250.0, 246.0, "Foo")
        assertTrue { qTree.find(150.0, 120.0).isEmpty() }
    }


    @Test
    @Disabled
    fun performanceTest() {
        val qTree = Quadtree<String>()
        val entries = (0..8999).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            val w = Random.nextDouble(100.0, 300.0)
            val h = Random.nextDouble(100.0, 300.0)
            Entry(x, y, x + w, y + h, UUID.randomUUID().toString())
        }
        entries.forEach { (minX, minY, maxX, maxY, value) ->
            qTree.insert(minX, minY, maxX, maxY, value)
        }
        val points = (0..999).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            x to y
        }
        repeat(300) {
            points.forEach { (x, y) ->
                qTree.find(x, y)
                qTree.find2(x, y)
            }
        }
        println("find1")
        testPerformance(99) {
            points.forEach { (x, y) ->
                qTree.find(x, y)
            }
        }
        println("find2")
        testPerformance(99) {
            points.forEach { (x, y) ->
                qTree.find2(x, y)
            }
        }

        println("iterating all entries")
        testPerformance(99) {
            points.forEach { (x, y) ->
                entries.filter { it.contains(x, y) }.map { it.value }
            }
        }

        println("Testing find by rect")
        val boxes = (0..200).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            val w = Random.nextDouble(100.0, 300.0)
            val h = Random.nextDouble(100.0, 300.0)
            listOf(x, y, x + w, y + h)
        }
        println("tree")
        testPerformance(99) {
            boxes.forEach { (x, y, a, b) ->
                qTree.find(x, y, a, b)
            }
        }
        println("list")
        testPerformance(99) {
            boxes.forEach { (x, y, a, b) ->
                entries.filter { it.intersects(x, y, a, b) }.map { it.value }
            }
        }
        points.forEach { (x, y) ->
            assertTrue {
                qTree.find(x, y).sorted() == entries.filter { it.contains(x, y) }.map { it.value }.sorted()
            }
            assertTrue {
                qTree.find(x, y).sorted() == qTree.find2(x, y).sorted()
            }
        }
        boxes.forEach { (x, y, a, b) ->
            assertTrue {
                qTree.find(x, y, a, b).sorted() == entries.filter { it.intersects(x, y, a, b) }.map { it.value }
                    .sorted()
            }

        }
    }

    private fun testPerformance(times: Int, toRun: () -> Unit) {
        printTimes((0 until times).map {
            measureTimeMillis(toRun)
        })
    }

    private fun printTimes(times: List<Long>) {
        println("Max: " + times.max())
        println("Min: " + times.min())
        println("Avg: " + times.average())
        println("Sum: " + times.sum())
    }
}