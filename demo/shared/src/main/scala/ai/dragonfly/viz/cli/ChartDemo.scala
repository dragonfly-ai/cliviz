package ai.dragonfly.viz.cli

import scala.collection.mutable
import ai.dragonfly.math.*
import Random.*
import interval.*
import Interval.*
import vector.*
import Vector.*
import ai.dragonfly.democrossy.Demonstration

import Console.{BOLD, RESET}

object ChartDemo extends Demonstration {

  val r = defaultRandom

  def demo():Unit = {

    val lineChart:Chart = Chart("Test Line Chart", "X", "Y", `[]`(-10.0, 15.0), `[]`(-5.0, 3.0), 200, 150)

    var theta:Double = 0.0
    val increment:Double =  Math.PI / 10

    val lines:Array[(Vector2, Double)] = Array[(Vector2, Double)](
      (Vector2(-9.0, 0.0), -1.25),
      (Vector2(-7.0, 0.0), -1.0),
      (Vector2(-5.0, 0.0), -0.75),
      (Vector2(-3.0, 0.0), -0.5),
      (Vector2(-1.0, 0.0), -0.25),
      (Vector2(0.0, -1.0), 0.0),
      (Vector2(1.0, 0.0), 0.25),
      (Vector2(2.0, 0.0), 0.5),
      (Vector2(3.0, 0.0), 0.75),
      (Vector2(4.0, 0.0), 1.0),
      (Vector2(5.0, 0.0), 1.25)
    )

    for ((point:Vector2, slope:Double) <- lines) {
      val b: Double = (-point.x * slope) + point.y
      val title:String = s" Y = ${if (slope != 0.0) {
        "%4.2f".format(slope) + "X"
      } else ""} ${if (b == 0.0) "" else if (b > 0.0) s" + ${Math.abs(b)}" else s"- ${Math.abs(b)}"} "

      lineChart.line(
        point,
        slope,
        title
      )
    }

    println(lineChart)

    val scatterPlot:Chart = Chart("Test Scatter Plot", "X", "Y", `[]`(-10.0, 10.0), `[]`(-10.0, 10.0), 100, 100)
    val v2s:Array[Vector2] = new Array(50)
    for (i <- v2s.indices) v2s(i) = r.nextVector2(20).subtract(Vector2(10, 10))
    scatterPlot.scatter("Scatter 1", v2s:_*)
    println(scatterPlot)

    val scatterPlot1:Chart = Chart("Test Connected Scatter Plot", "X", "Y", `[]`(-10.0, 10.0), `[]`(-10.0, 10.0), 100, 100)
    for (i <- v2s.indices) v2s(i) = Vector2(
      (i * (20.0 / v2s.length)) - 10.0,
      (r.nextDouble() * (20.0*(i+1.0) / v2s.length)) - (10.0*(i+1.0) / v2s.length)
    )
    scatterPlot1.connectedScatter("Connected Scatter 1", v2s:_*)
    println(scatterPlot1)

    val regressionPlot:Chart = Chart("Test Regression Plot", "Hours Studying", "Grade", `[]`(-10.0, 10.0), `[]`(-5.0, 5.0), 100, 50)

    val point:Vector2 = Vector2(0, -1)
    val slope:Double = 1.0 / 3.0
    val b:Double = (-point.x * slope) + point.y

    regressionPlot.line(point, slope, "Theory")

    var x:Double = regressionPlot.conf.domain.min
    val step = (regressionPlot.conf.domain.norm) / v2s.length
    for (i <- v2s.indices) {
      v2s(i) = Vector2(x, (slope * x) + b + (4.0 * (r.nextDouble() - 0.5)))
      //println(v2s(i))
      x = x + step
    }
    regressionPlot.scatter("Practice", v2s:_*)
    println(regressionPlot)

    for (i <- 0 until 10) {
      println(
        Chart("Axis Test", "", "X", "Y", `[]`(-5.0, 5.0), `[]`(-25.0, 25.0), 150 + i, 110 + i)
          .lineSegment(Vector2(-5, 2), Vector2(-4, 2), "2")
          .lineSegment(Vector2(-3, 1), Vector2(-2, 1), "1")
          .lineSegment(Vector2(-1, 0), Vector2(1, 0), "0")
          .lineSegment(Vector2(2, -1), Vector2(3, -1), "1")
          .lineSegment(Vector2(4, -2), Vector2(5, -2), "2")
          .lineSegment(Vector2(0, -20), Vector2(0, -10), "0")
      )
    }

  }

  def name: String = "Chart"

}
