package ai.dragonfly.viz.cli

import ai.dragonfly.math.*
import ai.dragonfly.math.Random.*
import ai.dragonfly.math.interval.*
import ai.dragonfly.math.interval.Interval.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.vector.Vector.*
import bridge.array.*

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


  def mapToImageSpace(v:Vector2):Vector2 = Vector2(
    (scaleX * (v.x - domain.min)) + aXa,
    (scaleY * (v.y - range.min)) + aYa
  )

  val cimg:CLImg = CLImg(width, height)

  // vertical axis ?
  if (domain.rangeContains(0.0)) {

    glyphLineSegment(
      Vector2(0.0, range.min),
      Vector2(0.0, range.MAX),
      Glyph.axis
    )

  }

  // Horizontal Axis? "⃨⃛" "͞"
  if (range.rangeContains(0.0)) {
    glyphLineSegment(
      Vector2(domain.min, 0.0),
      Vector2(domain.MAX, 0.0),
      Glyph.axis
    )
  }

  val leftPaddingWidth: Int = Math.max(Math.max(verticalLabel.length, range.MAX.toString.length), range.min.toString.length)

  private val legend:mutable.TreeMap[String, Glyph] = mutable.TreeMap[String, Glyph]()


  def glyphLineSegment(p1: Vector2, p2: Vector2, glyph:Glyph):Chart = {
    val unitWidth:Int = 2
    val unitHeight:Int = 4

    val pi1 = mapToImageSpace(p1)
    val start:Vector2 = Vector2( pi1.x / unitWidth, pi1.y / unitHeight)

    val pi2 = mapToImageSpace(p2)
    val end:Vector2 = Vector2(pi2.x / unitWidth, pi2.y / unitHeight)

    val hm:mutable.TreeMap[Int, Interval[Int]] = mutable.TreeMap[Int, Interval[Int]]()

    ai.dragonfly.math.geometry.Line.trace2D(start, end, (dX:Int, dY:Int) => {
      val xi:Int = dX * unitWidth
      val yi:Int = (height - 1) - (dY * unitHeight)
      val interval = hm.getOrElseUpdate(yi, `[]`(xi, xi))
      hm.put(yi, `[]`[Int](Math.min(interval.min, xi), Math.max(interval.MAX, xi)))
    })

    for ((yi:Int, xiv:Interval[Int]) <- hm) {
      val middle:Int = (xiv.min + xiv.MAX) / 2
      var s:Int = 1
      val step = 6
      var left:Int = xiv.min + (s*step)
      var right:Int = xiv.MAX - (s*step)

      var drew = false
      while (right - left > unitWidth) {
        cimg.setGlyph(left, yi, glyph)
        cimg.setGlyph(right, yi, glyph)

        s = s + 1

        left = xiv.min + (s*step)
        right = xiv.MAX - (s*step)
        drew = true
      }
      if (!drew) cimg.setGlyph(middle, yi, glyph)
    }

    this
  }

  def glyphLine(point: Vector2, slope:Double, name:String, glyph:Glyph):Chart = {
    val b:Double = (-point.x * slope) + point.y

    val start:Vector2 = Vector2(domain.min, domain.min * slope + b)
    val end:Vector2 = Vector2(domain.MAX, domain.MAX * slope + b)

    glyphLineSegment(start, end, glyph)
  }

  private var maxItemNameLength:Int = 0

  def lineSegment(p1: Vector2, p2:Vector2, name:String):Chart = {
    val glyph = legend.getOrElseUpdate(name, Glyph(legend.size))
    maxItemNameLength = Math.max(maxItemNameLength, name.length + 2)

    if (glyph.overlay) {
      glyphLineSegment(p1, p2, glyph)
    } else {

      val start:Vector2 = mapToImageSpace(p1)
      val end:Vector2 = mapToImageSpace(p2)

      ai.dragonfly.math.geometry.Line.trace2D(start, end, (dX:Int, dY:Int) => {
        cimg.setPixel(dX, (cimg.height - 1) - dY, glyph.color)
      })
      this
    }
  }

  def line(point: Vector2, slope:Double, name:String):Chart = {
    val glyph = legend.getOrElseUpdate(name, Glyph(legend.size))
    maxItemNameLength = Math.max(maxItemNameLength, name.length + 2)

    if (glyph.overlay) {
      glyphLine(point, slope, name, glyph)
    } else {
      val b:Double = (-point.x * slope) + point.y

      val start:Vector2 = Vector2(domain.min, domain.min * slope + b)
      val end:Vector2 = Vector2(domain.MAX, domain.MAX * slope + b)

      lineSegment(start, end, name)
    }
  }

  private def plotGlyph(glyph:Glyph)(p:Vector2): Unit = {
    val pT = mapToImageSpace(p)
    cimg.setGlyph(
      pT.x.toInt,
      (cimg.height - 1) - pT.y.toInt,
      glyph
    )
  }

  private def plotPixel(glyph:Glyph)(p:Vector2):Unit = {
    val pT = mapToImageSpace(p)
    cimg.setPixel(
      pT.x.toInt,
      (cimg.height - 1) - pT.y.toInt,
      glyph.color
    )
  }

  def scatter(name:String, points:Vector2*):Chart = {
    val glyph = legend.getOrElseUpdate(name, Glyph(legend.size))
    maxItemNameLength = Math.max(maxItemNameLength, name.length + 2)

    points.foreach(
      if (glyph.overlay) (p:Vector2) => plotGlyph(glyph)(p)
      else (p:Vector2) => plotPixel(glyph)(p)
    )

    this
  }

  def connectedScatter(name:String, points:Vector2*):Chart = {
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
    for (i <- 0 until leftPaddingWidth - s.length) sb.append(" ")
    sb.append(s)
  }

  def chartPadLeft(s:String)(using sb:SegmentedString):SegmentedString = {
    for (i <- 0 until ((width/2) - s.length) / 2) sb.append("⠀")
    sb.append(s)
  }

  def topBorder(using sb:SegmentedString):SegmentedString = {
    sb.append(" ⢀")
    for (i <- 0 until width/2) sb.append("⣀")
    sb.append("⡀")
  }

  def bottomBorder(using sb:SegmentedString):SegmentedString = {
    sb.append(" ⠈")
    for (i <- 0 until width/2) sb.append("⠉")
    sb.append("⠁")
  }

  override def toString: String = {
    given ss:SegmentedString = new SegmentedString().append(RESET)
    //ss.append(RESET).append(s"$zeroX and $zeroY ${mapFromImageSpace(Vector2(zeroX, zeroY))} and $domain x $range\n")
    padLeft("")
    chartPadLeft(title).append("\n")
    padLeft("")
    topBorder.append("\n")
    val lines:ARRAY[String] = cimg.lines()
    padLeft(range.MAX.toString).append(" ⢸").append(lines.head).append(RESET).append("⡇\n")
    val litr:Iterator[(String, Glyph)] = legend.iterator
    val footerLegend:String = if (legend.size > lines.length) {
      given lsb:SegmentedString = SegmentedString()
      padLeft("")
      var lineLength:Int = 0
      while(litr.hasNext) {
        val (itemName:String, itemGlyph:Glyph) = litr.next()
        val legendItem:String = s" ${itemGlyph.asIcon} $itemName"
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
    for (i <- 1 until lines.length - 1) {
      val l = if (i == lines.length / 2) verticalLabel else ""
      padLeft(l).append(" ⢸").append(lines(i)).append(RESET).append("⡇⠀")
      if (litr.hasNext) {
        val (itemName, itemGlyph) = litr.next()
        ss.append(s" ${itemGlyph.asIcon} $itemName")
      }
      ss.append("\n")
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
