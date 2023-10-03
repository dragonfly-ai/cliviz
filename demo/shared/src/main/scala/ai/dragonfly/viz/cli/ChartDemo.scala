package ai.dragonfly.viz.cli

import narr.*
import ai.dragonfly.math.*
import Random.*
import interval.*
import Interval.*
import vector.*
import Vec.*
import ai.dragonfly.democrossy.Demonstration

import scala.util.Random

object ChartDemo extends Demonstration {

  val r: Random = defaultRandom

  def demo():Unit = {

    val lineChart:Chart = Chart("Color Coded Line Chart with Diverse Glyphs", "X", "Y", `[]`(-10.0, 15.0), `[]`(-5.0, 3.0), 100, 50)

    val lines:NArray[(Vec[2], Double)] = NArray[(Vec[2], Double)](
      (Vec[2](-9.0, 0.0), -1.25),
      (Vec[2](-7.0, 0.0), -1.0),
      (Vec[2](-5.0, 0.0), -0.75),
      (Vec[2](-3.0, 0.0), -0.5),
      (Vec[2](-1.0, 0.0), -0.25),
      (Vec[2](0.0, -1.0), 0.0),
      (Vec[2](1.0, 0.0), 0.25),
      (Vec[2](2.0, 0.0), 0.5),
      (Vec[2](3.0, 0.0), 0.75),
      (Vec[2](4.0, 0.0), 1.0),
      (Vec[2](5.0, 0.0), 1.25)
    )

    for ((point:Vec[2], slope:Double) <- lines) {
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

    val scatterPlot:Chart = Chart("Scatter Plot", "X", "Y", `[]`(-10.0, 10.0), `[]`(-10.0, 10.0), 100, 50)
    val v2s:Array[Vec[2]] = new Array(50)
    for (i <- v2s.indices) v2s(i) = r.nextVec[2](20).subtract(Vec[2](10, 10))
    scatterPlot.scatter("f(x,y) > 0", v2s:_*)
    println(scatterPlot)

    val scatterPlot1:Chart = Chart("Connected Scatter Plot", "X", "Y", `[]`(-10.0, 10.0), `[]`(-10.0, 10.0), 100, 50)
    for (i <- v2s.indices) v2s(i) = Vec[2](
      (i * (20.0 / v2s.length)) - 10.0,
      (r.nextDouble() * (20.0*(i+1.0) / v2s.length)) - (10.0*(i+1.0) / v2s.length)
    )
    scatterPlot1.connectedScatter("Sea Level", v2s:_*)
    println(scatterPlot1)

    val regressionPlot:Chart = Chart("Regression Plot: Combined Scatter and Line Plots", "Net Worth", "Happiness", `[]`(-10.0, 10.0), `[]`(-5.0, 5.0), 100, 50)

    val point:Vec[2] = Vec[2](0, -1)
    val slope:Double = 1.0 / 3.0
    val b:Double = (-point.x * slope) + point.y

    regressionPlot.line(point, slope, "Theory")

    var x:Double = regressionPlot.conf.domain.min
    val step = regressionPlot.conf.domain.norm / v2s.length
    for (i <- v2s.indices) {
      v2s(i) = Vec[2](x, (slope * x) + b + (4.0 * (r.nextDouble() - 0.5)))
      //println(v2s(i))
      x = x + step
    }
    regressionPlot.scatter("Practice", v2s:_*)
    println(regressionPlot)

//    for (i <- 0 until 10) {
//      println(
//        Chart("Axis Test", "", "X", "Y", `[]`(-5.0, 5.0), `[]`(-25.0, 25.0), 150 + i, 110 + i)
//          .lineSegment(Vec[2](-5, 2), Vec[2](-4, 2), "2")
//          .lineSegment(Vec[2](-3, 1), Vec[2](-2, 1), "1")
//          .lineSegment(Vec[2](-1, 0), Vec[2](1, 0), "0")
//          .lineSegment(Vec[2](2, -1), Vec[2](3, -1), "1")
//          .lineSegment(Vec[2](4, -2), Vec[2](5, -2), "2")
//          .lineSegment(Vec[2](0, -20), Vec[2](0, -10), "0")
//      )
//    }

  }

  def name: String = "Chart"

}
