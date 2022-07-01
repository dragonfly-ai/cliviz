package ai.dragonfly.viz.cli

import ai.dragonfly.math.*
import Random.*
import ai.dragonfly.democrossy.Demonstration
import vector.*
import geometry.Line
import visualization.ConsoleImage.{BLACK, GRAY, WHITE, colorBytes}

import scala.collection.mutable

object CLImgDemo extends Demonstration {

  def randomSpiral(ci:CLImg, color:Int):CLImg = {
    val p:Vector2 = defaultRandom.next(Vector2(ci.width, ci.height))
    val v:Vector2 = Vector2(1.0, 0.0)
    val s = 1.002 + (Math.random() * 0.0002)
    val dT:Double = (Math.random() - 0.5) * Math.PI / 100.0
    var pV:Vector2 = v.copy()
    val end = squareInPlace(ci.width) + squareInPlace(ci.height)
    while (v.magnitudeSquared < end){
      pV = p + v
      v.rotate(dT).scale(s)
      Line.trace2D(
        pV,
        p + v,
        (dX:Int, dY:Int) => {
          if (dX >= 0 && dX < ci.width) {
            if (dY >= 0 && dY < ci.height) {
              ci.setPixel(dX, dY, color)
            }
          }
        }
      )
    }
    ci
  }

  def demo():Unit = {
    val ci: CLImg = new CLImg(200, 150)
    for (i <- 0 until 10) {
      randomSpiral(ci, defaultRandom.nextInt(CLImg.colorBytes.length))
    }

    ci.print()

    println(s"Layers\n")
    //println("What is going on here?")

    for (channel <- ci.channels) {
      for (line <- channel) {
        println(line)
      }
    }
  }

  def name: String = "CLImg"
}
