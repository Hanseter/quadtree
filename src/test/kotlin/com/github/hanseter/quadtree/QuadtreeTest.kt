package com.github.hanseter.quadtree

import com.github.hanseter.quadtree.impl.Entry
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

class QuadtreeTest {

    private val qTree = Quadtree<String>()
    private val qTree2 = Quadtree2<String>()

    @Test
    fun insert() {
        Thread.sleep(20000)
        val entries = (0..8999).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            val w = Random.nextDouble(100.0, 300.0)
            val h = Random.nextDouble(100.0, 300.0)
            Entry(x, y, x + w, y + h, UUID.randomUUID().toString())
        }
//        println("1")
//        testPerformance(50) {
//            entries.forEach { (minX, minY, maxX, maxY, value) ->
//                qTree.insert(minX, minY, maxX, maxY, value)
//            }
//        }
//        println("2")
//        testPerformance(50) {
//            entries.forEach { (minX, minY, maxX, maxY, value) ->
//                qTree2.insert(minX, minY, maxX, maxY, value)
//            }
//        }
        entries.forEach { (minX, minY, maxX, maxY, value) ->
            qTree.insert(minX, minY, maxX, maxY, value)
            qTree2.insert(minX, minY, maxX, maxY, value)
        }
        val points = (0..999).map {
            val x = Random.nextDouble(-5000.0, 5000.0)
            val y = Random.nextDouble(-5000.0, 5000.0)
            x to y
        }
        println("1")
        testPerformance(99) {
            points.forEach { (x, y) ->
                qTree.find(x, y)
            }
        }
        println("2")
        testPerformance(99) {
            points.forEach { (x, y) ->
                qTree2.find(x, y)
            }
        }
        println("3")
        testPerformance(99) {
            points.forEach { (x, y) ->
                entries.filter { it.contains(x, y) }.map { it.value }
            }
        }
        points.forEach { (x, y) ->
            assertTrue {
                qTree.find(x, y).sorted() == entries.filter { it.contains(x, y) }.map { it.value }.sorted()
            }
            assertTrue {
                qTree.find(x, y).sorted() == qTree2.find(x, y).sorted()
            }
        }
    }

    fun testPerformance(times: Int, toRun: () -> Unit) {
        printTimes((0 until times).map {
            measureTimeMillis(toRun)
        })
    }

    fun printTimes(times: List<Long>) {
        println("Max: " + times.max())
        println("Min: " + times.min())
        println("Avg: " + times.average())
        println("Sum: " + times.sum())
    }

    @Test
    fun removesOnlySameInstances() {
        qTree.insert(100.0, 100.0, 250.0, 146.0, "Foo")
        assertTrue { qTree.find(150.0, 120.0).single() == "Foo" }
        qTree.remove("Foobar".take(3))
        assertTrue { qTree.find(150.0, 120.0).single() == "Foo" }
        qTree.remove("Foo")
        assertTrue { qTree.find(150.0, 120.0).isEmpty() }
    }
}