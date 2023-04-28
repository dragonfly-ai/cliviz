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
