/*
 * Copyright 2023 dragonfly.ai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.dragonfly.viz.cli

import ai.dragonfly.math.*
import ai.dragonfly.math.interval.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.vector.Vec.*
import narr.*

import scala.Console.RESET
import scala.collection.mutable

object Chart {

  def apply(title:String, horizontalLabel:String, verticalLabel:String, domain:Interval[Double], range:Interval[Double], width:Int, height:Int):Chart = {
    new Chart( ChartConfig(title, "", horizontalLabel, verticalLabel, domain, range, width, height) )
  }

  def apply(title:String, caption:String, horizontalLabel:String, verticalLabel:String, domain:Interval[Double], range:Interval[Double], width:Int, height:Int):Chart = {
    new Chart( ChartConfig(title, caption, horizontalLabel, verticalLabel, domain, range, width, height) )
  }
}


object ChartConfig {
  def apply(title:String, caption:String, horizontalLabel:String, verticalLabel:String, domain:Interval[Double], range:Interval[Double], width:Int, height:Int):ChartConfig = {

    // adjust image dimensions to multiples of unit dimensions
    val w:Int = if (width % 2 == 0) width else width + 1
    val h:Int = height % 4 match {
      case 0 => height // no adjustment needed
      case dh => height + 4 - dh
    }

    val t = if (title.length > w * 2) s"${title.substring(0, (w * 2) - 5)} ..." else title
    val c = caption
    val maxHl = w - domain.toString.length
    val hl = if (horizontalLabel.length > maxHl) {
      horizontalLabel.substring(maxHl - 4) + " ..."
    } else horizontalLabel

    val vl = if (verticalLabel.length > 20) {
      verticalLabel.substring( 16 ) + " ..."
    } else verticalLabel

    val d = domain
    val r = range

    new ChartConfig {
      override val title:String = t
      override val caption:String = c
      override val horizontalLabel:String = hl
      override val verticalLabel:String = vl
      override val domain: Interval[Double] = d
      override val range: Interval[Double] = r
      override val width:Int = w
      override val height:Int = h
    }
  }
}

trait ChartConfig {
  def title:String
  def caption:String
  def horizontalLabel:String
  def verticalLabel:String
  def domain:Interval[Double]
  def range:Interval[Double]
  def width:Int
  def height:Int
}

case class Chart( conf:ChartConfig ) {
  import conf.*

  val scaleX:Double = width / domain.norm
  val scaleY:Double = height / range.norm

  val aXa:Int = if ((-domain.min * scaleX).toInt % 2 == 0) 1 else 0
  val aYa:Int = (-range.min * scaleY).toInt % 4 match {
    case 0 => 2
    case 1 => 1
    case 2 => 0 // no adjustment needed
    case 3 => -1
  }


  def mapToImageSpace(v:Vec[2]):Vec[2] = Vec[2](
    (scaleX * (v.x - domain.min)) + aXa,
    (scaleY * (v.y - range.min)) + aYa
  )

  val leftPaddingWidth: Int = Math.max(Math.max(verticalLabel.length, range.MAX.toString.length), range.min.toString.length)

  private val legend: mutable.TreeMap[String, Int] = mutable.TreeMap[String, Int]()
  legend.put("Axis", CLImg.WHITE)

  def lookUpColor(name: String): Int = legend.getOrElseUpdate(
    name, if (legend.size < 7) legend.size else 1 * legend.size % 7
  )

  val cimg:CLImg = CLImg(width, height)

  // vertical axis ?
  if (domain.rangeContains(0.0)) {

    lineSegment(
      Vec[2](0.0, range.min),
      Vec[2](0.0, range.MAX),
      "Axis"
    )

  }

  // Horizontal Axis? "⃨⃛" "͞"
  if (range.rangeContains(0.0)) {
    lineSegment(
      Vec[2](domain.min, 0.0),
      Vec[2](domain.MAX, 0.0),
      "Axis"
    )
  }

  private var maxItemNameLength:Int = 0

  def lineSegment(p1: Vec[2], p2:Vec[2], name:String):Chart = {
    maxItemNameLength = Math.max(maxItemNameLength, name.length + 2)

    val start:Vec[2] = mapToImageSpace(p1)
    val end:Vec[2] = mapToImageSpace(p2)

    ai.dragonfly.math.geometry.Line.trace2D(start, end, (dX:Int, dY:Int) => {
      cimg.setPixel(dX, (cimg.height - 1) - dY, lookUpColor(name))
    })
    this
  }

  def line(point: Vec[2], slope:Double, name:String):Chart = {
    maxItemNameLength = Math.max(maxItemNameLength, name.length + 2)

    val b:Double = (-point.x * slope) + point.y

    val start:Vec[2] = Vec[2](domain.min, domain.min * slope + b)
    val end:Vec[2] = Vec[2](domain.MAX, domain.MAX * slope + b)

    lineSegment(start, end, name)
  }

  private def plotPixel(c:Int)(p:Vec[2]):Unit = {
    val pT = mapToImageSpace(p)
    cimg.setPixel(
      pT.x.toInt,
      (cimg.height - 1) - pT.y.toInt,
      c
    )
  }

  def scatter(name:String, points:Vec[2]*):Chart = {
    maxItemNameLength = Math.max(maxItemNameLength, name.length + 2)

    points.foreach( (p:Vec[2]) => plotPixel(lookUpColor(name))(p) )

    this
  }

  def connectedScatter(name:String, points:Vec[2]*):Chart = {
    var p = points.head
    var tail = points.tail

    while(tail.nonEmpty) {
      lineSegment(p, tail.head, name)

      p = tail.head
      tail = tail.tail
    }
    this
  }

  def padLeft(s:String)(using sb:SegmentedString):SegmentedString = {
    var i:Int = 0
    while (i < leftPaddingWidth - s.length) {
      sb.append(" ")
      i = i + 1
    }
    sb.append(s)
  }

  def chartPadLeft(s:String)(using sb:SegmentedString):SegmentedString = {
    var i: Int = 0
    while (i < ((width/2) - s.length) / 2) {
      sb.append("⠀")
      i = i + 1
    }
    sb.append(s)
  }

  def topBorder(using sb:SegmentedString):SegmentedString = {
    sb.append(" ⢀")
    var i: Int = 0
    while (i < width/2) {
      sb.append("⣀")
      i = i + 1
    }
    sb.append("⡀")
  }

  def bottomBorder(using sb:SegmentedString):SegmentedString = {
    sb.append(" ⠈")
    var i: Int = 0
    while (i < width/2) {
      sb.append("⠉")
      i = i + 1
    }
    sb.append("⠁")
  }

  inline def makeIcon(color:Int):String = s"${CLImg.colorBytes(color)}⠒⠀$RESET"

  override def toString: String = {
    given ss:SegmentedString = new SegmentedString().append(RESET)
    //ss.append(RESET).append(s"$zeroX and $zeroY ${mapFromImageSpace(Vec[2](zeroX, zeroY))} and $domain x $range\n")
    padLeft("")
    chartPadLeft(title).append("\n")
    padLeft("")
    topBorder.append("\n")
    val lines:NArray[String] = cimg.lines()
    padLeft(range.MAX.toString).append(" ⢸").append(lines.head).append(RESET).append("⡇\n")
    val litr:Iterator[(String, Int)] = legend.iterator
    val footerLegend:String = if (legend.size > lines.length) {
      given lsb:SegmentedString = SegmentedString()
      padLeft("")
      var lineLength:Int = 0
      while(litr.hasNext) {
        val (itemName:String, c:Int) = litr.next()
        val legendItem:String = s" ${makeIcon(c)} $itemName"
        if (lineLength + legendItem.length + 4 > width) {  // maxItemNameLength
          lsb.append("\n")
          padLeft("")
          lineLength = legendItem.length + 4
        } else {
          lineLength = lineLength + legendItem.length + 4
        }
        lsb.append(legendItem).append("  ")
      }
      lsb.toString()
    } else ""
    var i: Int = 1
    while (i < lines.length - 1) {
      val l = if (i == lines.length / 2) verticalLabel else ""
      padLeft(l).append(" ⢸").append(lines(i)).append(RESET).append("⡇⠀")
      if (litr.hasNext) {
        val (itemName, c) = litr.next()
        ss.append(s" ${makeIcon(c)} $itemName")
      }
      ss.append("\n")
      i = i + 1
    }
    padLeft(range.min.toString).append(" ⢸").append(lines.last).append(RESET).append("⡇ \n")
    padLeft("")
    bottomBorder.append("\n")
    padLeft("").append(domain.min)
    chartPadLeft(horizontalLabel)
    chartPadLeft(domain.MAX.toString)
      .append("\n")
      .append(footerLegend).append("\n")
      .toString()
  }

}
