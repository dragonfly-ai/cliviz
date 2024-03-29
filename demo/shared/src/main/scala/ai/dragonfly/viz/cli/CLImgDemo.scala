package ai.dragonfly.viz.cli

import ai.dragonfly.math.*
import Random.*
import ai.dragonfly.democrossy.Demonstration
import vector.*
import Vec.*
import geometry.Line
import ai.dragonfly.viz.cli.CLImg.colorBytes

object CLImgDemo extends Demonstration {

  def randomSpiral(ci:CLImg, color:Int):CLImg = {
    val p:Vec[2] = defaultRandom.nextVec[2](Vec[2](ci.width, ci.height))
    val v:Vec[2] = Vec[2](1.0, 0.0)
    val s = 1.002 + (Math.random() * 0.0002)
    val dT:Double = (Math.random() - 0.5) * Math.PI / 100.0
    var pV:Vec[2] = v.copy
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
    val ci: CLImg = new CLImg(100, 50)
    for (i <- 0 until 5) {
      randomSpiral(ci, i % CLImg.colorBytes.length)
    }

    println(ci)

    println(s"The image above split into independent color channels: \n")

    for (channel <- ci.channels) {
      for (line <- channel) {
        println(line)
      }
    }
  }

  def name: String = "CLImg"
}
