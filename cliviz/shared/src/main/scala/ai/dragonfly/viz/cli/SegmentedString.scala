package ai.dragonfly.viz.cli


import scala.collection.mutable.ListBuffer

class SegmentedString () {
  private var length:Int = 0
  private val segments:ListBuffer[Array[Byte]] = ListBuffer[Array[Byte]]()

  def append(a:Any):SegmentedString = append(a.toString)

  def append(s:String):SegmentedString = synchronized {
    if (s.nonEmpty) {
      val bytes = s.getBytes
      segments += bytes
      length += bytes.length
    }
    this
  }

  override def toString: String = {
    val bytes: Array[Byte] = new Array[Byte](length)
    var bi:Int = 0
    for (segment <- segments) {
      var i = 0
      while (i < segment.length) {
        bytes(bi + i) = segment(i)
        i += 1
      }
      bi += segment.length
    }
    new String(bytes)
  }
}
