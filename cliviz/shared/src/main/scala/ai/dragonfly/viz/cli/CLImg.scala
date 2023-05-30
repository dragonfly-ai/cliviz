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
import ai.dragonfly.math.Random.*
import ai.dragonfly.math.geometry.Line
import ai.dragonfly.math.vector.*
import narr.*

import scala.collection.mutable

object CLImg {

  val brailleBytes:NArray[String] = NArray[String](
    //"⠈",
    "⠀", "⠁", "⠂", "⠃", "⠄", "⠅", "⠆", "⠇", "⡀", "⡁", "⡂", "⡃", "⡄", "⡅", "⡆", "⡇", "⠈", "⠉", "⠊", "⠋", "⠌", "⠍", "⠎", "⠏", "⡈", "⡉", "⡊", "⡋", "⡌", "⡍", "⡎", "⡏", "⠐", "⠑", "⠒", "⠓", "⠔", "⠕", "⠖", "⠗", "⡐", "⡑", "⡒", "⡓", "⡔", "⡕", "⡖", "⡗", "⠘", "⠙", "⠚", "⠛", "⠜", "⠝", "⠞", "⠟", "⡘", "⡙", "⡚", "⡛", "⡜", "⡝", "⡞", "⡟", "⠠", "⠡", "⠢", "⠣", "⠤", "⠥", "⠦", "⠧", "⡠", "⡡", "⡢", "⡣", "⡤", "⡥", "⡦", "⡧", "⠨", "⠩", "⠪", "⠫", "⠬", "⠭", "⠮", "⠯", "⡨", "⡩", "⡪", "⡫", "⡬", "⡭", "⡮", "⡯", "⠰", "⠱", "⠲", "⠳", "⠴", "⠵", "⠶", "⠷", "⡰", "⡱", "⡲", "⡳", "⡴", "⡵", "⡶", "⡷", "⠸", "⠹", "⠺", "⠻", "⠼", "⠽", "⠾", "⠿", "⡸", "⡹", "⡺", "⡻", "⡼", "⡽", "⡾", "⡿", "⢀", "⢁", "⢂", "⢃", "⢄", "⢅", "⢆", "⢇", "⣀", "⣁", "⣂", "⣃", "⣄", "⣅", "⣆", "⣇", "⢈", "⢉", "⢊", "⢋", "⢌", "⢍", "⢎", "⢏", "⣈", "⣉", "⣊", "⣋", "⣌", "⣍", "⣎", "⣏", "⢐", "⢑", "⢒", "⢓", "⢔", "⢕", "⢖", "⢗", "⣐", "⣑", "⣒", "⣓", "⣔", "⣕", "⣖", "⣗", "⢘", "⢙", "⢚", "⢛", "⢜", "⢝", "⢞", "⢟", "⣘", "⣙", "⣚", "⣛", "⣜", "⣝", "⣞", "⣟", "⢠", "⢠", "⢢", "⢣", "⢤", "⢥", "⢦", "⢧", "⣠", "⣡", "⣢", "⣣", "⣤", "⣥", "⣦", "⣧", "⢨", "⢩", "⢪", "⢫", "⢬", "⢭", "⢮", "⢯", "⣨", "⣩", "⣪", "⣫", "⣬", "⣭", "⣮", "⣯", "⢰", "⢱", "⢲", "⢳", "⢴", "⢵", "⢶", "⢷", "⣰", "⣱", "⣲", "⣳", "⣴", "⣵", "⣶", "⣷", "⢸", "⢹", "⢺", "⢻", "⢼", "⢽", "⢾", "⢿", "⣸", "⣹", "⣺", "⣻", "⣼", "⣽", "⣾", "⣿",
  )

  // Red + Green = Yellow
  // Red + Blue = Magenta
  // Green + Blue = Cyan
  // Red + Green + Blue = White

  val ALL:Int = -1

  val BLACK:Int = 0     // 0000
  val RED:Int = 1       // 0001
  val GREEN:Int = 2     // 0010
  val YELLOW:Int = 3    // 0011
  val BLUE:Int = 4      // 0100
  val MAGENTA:Int = 5   // 0101
  val CYAN:Int = 6      // 0110
  val WHITE:Int = 7     // 0111

  val colorBytes:NArray[String] = NArray[String](
    Console.BLACK,
    Console.RED,
    Console.GREEN,
    Console.YELLOW,
    Console.BLUE,
    Console.MAGENTA,
    Console.CYAN,
    //Console.RESET, // white on dark terminals, black on light ones.
    Console.WHITE  // White is actually gray.
  )

}

class CLImg(val width:Int, val height:Int) {

  import Console.{BOLD, RESET}
  import CLImg.*

  private val w:Int = width >> 1   // width / 2
  private val h:Int = height >> 2  // height / 4

  val pixelCount:Int = w * (h + 1)

  inline def linearIndexOf(x: Int, y: Int): Int = {
    var out = -1
    if (x > -1) {  // && y > -1  // left out y positive check because negative y will always cause negative output
      val x0 = x >> 1
      if (x0 < w) {
        val y0 = y >> 2
        if (y0 < h ) out = x0 + (y0 * w)
      }
    }
    out
  }

  val blackChannel:NArray[Int] = NArray.fill(pixelCount)(0)
  val redChannel:NArray[Int] = NArray.fill(pixelCount)(0)
  val greenChannel:NArray[Int] = NArray.fill(pixelCount)(0)
  val blueChannel:NArray[Int] = NArray.fill(pixelCount)(0)

  val layer:NArray[List[Int]] = NArray.fill(pixelCount)(List[Int]())

  inline def hasRed(color:Int):Boolean = color match {
    case RED => true
    case YELLOW => true
    case MAGENTA => true
    case WHITE => true
    case _ => false
  }

  inline def hasGreen(color:Int):Boolean = color match {
    case GREEN => true
    case YELLOW => true
    case CYAN => true
    case WHITE => true
    case _ => false
  }

  inline def hasBlue(color:Int):Boolean = color match {
    case BLUE => true
    case MAGENTA => true
    case CYAN => true
    case WHITE => true
    case _ => false
  }

  inline private def getColor(i:Int):Int = {
    var out = (if (redChannel(i) > 0) RED else BLACK) | (if (greenChannel(i) > 0) GREEN else BLACK) | (if (blueChannel(i) > 0) BLUE else BLACK)

    if (out != WHITE && layer(i).nonEmpty) {
      var gg:Boolean = false
      for (g <- layer(i)) {
        if (g != WHITE) out = out | g
        else gg = true
      }
      if (gg && out == BLACK) {
        out = WHITE
      }
    }

    out
  }

  def setPixel(x:Int, y:Int, color:Int):CLImg = {
    val i:Int = linearIndexOf(x, y)
    if (i > -1) {
      val pi:Int = (1 << (y % 4)) << (x % 2 * 4)
      color match {
        case BLACK => blackChannel(i) = blackChannel(i) | pi
        case _ =>
          if (hasRed(color)) redChannel(i) = redChannel(i) | pi
          if (hasGreen(color)) greenChannel(i) = greenChannel(i) | pi
          if (hasBlue(color)) blueChannel(i) = blueChannel(i) | pi
      }
      // erasing? (if (color == K) pixels(i) & ~byte else pixels(i) | byte)
    }
    this
  }

  def getPixelColor(x:Int, y:Int):Int = {

    val i:Int = linearIndexOf(x, y)
    if (i > -1) {
      val pi:Int = (1 << (y % 4)) << (x % 2 * 4)

      val out:Int = ( if (  (redChannel(i) ^ pi) > 0 ) 1 else 0 ) |
                    ( if ((greenChannel(i) ^ pi) > 0 ) 2 else 0 ) |
                    ( if ( (blueChannel(i) ^ pi) > 0 ) 4 else 0 )

      if (out == BLACK) {
        if ((blackChannel(i) ^ pi) > 0) BLACK
        else -1
      } else out
    } else -1

  }

  def setGlyph(x:Int, y:Int, color:Int):CLImg = {
    val i:Int = linearIndexOf(x, y)
    if (i > -1) layer(i) = color :: layer(i)
    this
  }

  def channels:NArray[NArray[String]] = NArray.tabulate(colorBytes.length)((channel:Int) => lines(channel) )

  def getPixelBytes(i:Int, channel:Int = ALL):Int = channel match {
    case ALL => redChannel(i) | greenChannel(i) | blueChannel(i) | blackChannel(i)
    case BLACK => blackChannel(i)
    case RED => redChannel(i)
    case GREEN => greenChannel(i)
    case YELLOW => redChannel(i) & greenChannel(i)
    case BLUE => blueChannel(i)
    case MAGENTA => redChannel(i) & blueChannel(i)
    case CYAN => greenChannel(i) & blueChannel(i)
    case WHITE => redChannel(i) & greenChannel(i) & blueChannel(i)
  }

  def lines(channel:Int = ALL):NArray[String] = {
    val out:NArray[String] = NArray.ofSize[String](h)
    var y:Int = 0
    while (y < h) {
      var lastColor:Int = -1
      val ss:SegmentedString = new SegmentedString()

      var x:Int = 0
      while (x < w) {

        val i:Int = (y * w) + x

        val pixelBytes = getPixelBytes(i, channel)
        val color:Int = if (channel == ALL) getColor(i) else channel

        val pattern = pixelBytes & 0x000000ff

        if (color != lastColor || color < 0) {
          lastColor = color
          ss.append(colorBytes(color))
          ss.append(brailleBytes(pattern))
        } else {
          ss.append(brailleBytes(pattern))
        }

        for (g <- layer(i)) {
          ss.append(g.toString)
        }
        x = x + 1
      }
      out(y) = ss.toString()
      y = y + 1
    }
    out
  }

  override def toString:String = {
    val ss:SegmentedString = new SegmentedString()
    val ls:NArray[String] = lines()
    for (line <- ls) {
      ss.append(line).append("\n")
    }
    ss.append(s"${Console.RESET}")
    ss.toString()
  }
}

/* Unicode Braille Characters for Plotting:

⠀
𝄛⠈⠐⠘⠠⠨⠰⠸⢀⢈⢐⢘⢠⢨⢰⢸
⠁⠉⠑⠙⠡⠩⠱⠹⢁⢉⢑⢙⢠⢩⢱⢹
⠂⠊⠒⠚⠢⠪⠲⠺⢂⢊⢒⢚⢢⢪⢲⢺
⠃⠋⠓⠛⠣⠫⠳⠻⢃⢋⢓⢛⢣⢫⢳⢻
⠄⠌⠔⠜⠤⠬⠴⠼⢄⢌⢔⢜⢤⢬⢴⢼
⠅⠍⠕⠝⠥⠭⠵⠽⢅⢍⢕⢝⢥⢭⢵⢽
⠆⠎⠖⠞⠦⠮⠶⠾⢆⢎⢖⢞⢦⢮⢶⢾
⠇⠏⠗⠟⠧⠯⠷⠿⢇⢏⢗⢟⢧⢯⢷⢿
⡀⡈⡐⡘⡠⡨⡰⡸⣀⣈⣐⣘⣠⣨⣰⣸
⡁⡉⡑⡙⡡⡩⡱⡹⣁⣉⣑⣙⣡⣩⣱⣹
⡂⡊⡒⡚⡢⡪⡲⡺⣂⣊⣒⣚⣢⣪⣲⣺
⡃⡋⡓⡛⡣⡫⡳⡻⣃⣋⣓⣛⣣⣫⣳⣻
⡄⡌⡔⡜⡤⡬⡴⡼⣄⣌⣔⣜⣤⣬⣴⣼
⡅⡍⡕⡝⡥⡭⡵⡽⣅⣍⣕⣝⣥⣭⣵⣽
⡆⡎⡖⡞⡦⡮⡶⡾⣆⣎⣖⣞⣦⣮⣶⣾
⡇⡏⡗⡟⡧⡯⡷⡿⣇⣏⣗⣟⣧⣯⣷⣿

⠁̇ ⠈⠂⠐᛫·𝆺𝆹⠄⠠.⡀⢀̣ [ ⃘][̊ ][⠀ͦ]

⠠
⠰
⢰or⠸
⢸
▕
▕·
▕⠆
▕⠇or⡆
▕⡇
▕▍
⣿︲⣿̣̇
⣿￤⣿
⣿︰⣿
⣿︙⣿
⣿⦙⣿
⃛⣿▕⣿
⣿▐⣿
⣿⋮⣿
⣿⁝⣿
⣿⁞⣿
⣿:⣿
⣿¦⣿
⣿┆⣿
⣿┊⣿
⣿╎⣿
⣿╏⣿
⣿┋⣿
⣿⁞⣿
⣿┆⣿⃓
⣿┊⣿
⣿‖⣿
⦀ ⦙
⣿·⣿:⣿︙⣿∵⣿∴⣿⃞
⢸⦙·⢸:⢸︙⢸∵⢸∴⢸
⢸⦙⡇·⡇:⡇︙̣᛬︙⡇∵⡇∴⡇
· :  ∵ ̣∴
︰ ︙ ̇᛬̣᛬
⋮ ⁝ ⁞ ◌
︲ ⁖ ⁘ ⁙ ⁛
… …⃛ ⦙⦙⦙⃨⦙⃒⃨⃜ [⦙⦙⦙⃨⦙]⃒
⋮⦙⃨⃛


̇̇̇̇

 ⁖ ⁘ ⁙ ⁛
◌

🁢🂠 🀆 🀫 🀕 🀘 🀞 🀠 🀡
𝄖 𝄗 𝄘 𝄙 𝄚 𝄛

⚊⚋𝌀

⚌⚍⚎
⚏𝌁𝌂
𝌃𝌄𝌅

☰ ☱ ☲ ☳
☴ ☵ ☶ ☷

䷀䷁䷂䷃䷄䷅䷆䷇
䷈䷉䷊䷋䷌䷍䷎䷏
䷐䷑䷒䷓䷔䷕䷖䷗
䷘䷙䷚䷛䷜䷝䷞䷟
䷠䷡䷢䷣䷤䷥䷦䷧
䷨䷩䷪䷫䷬䷭䷮䷯
䷰䷱䷲䷳䷴䷵䷶䷷
䷸䷹䷺䷻䷼䷽䷾䷿

𝌆𝌇𝌈𝌉𝌊𝌋𝌌𝌍𝌎𝌏𝌐𝌑𝌒𝌓𝌔𝌕𝌖𝌗𝌘𝌙
𝌚𝌛𝌜𝌝𝌞𝌟𝌠𝌡𝌢𝌣𝌤𝌥𝌦𝌧𝌨𝌩𝌪𝌫𝌬𝌭
𝌮𝌯𝌰𝌱𝌲𝌳𝌴𝌵𝌶𝌷𝌸𝌹𝌺𝌻𝌼𝌽𝌾𝌿𝍀𝍁
𝍂𝍃𝍄𝍅𝍆𝍇𝍈𝍉𝍊𝍋𝍌𝍍𝍎𝍏𝍐𝍑𝍒𝍓𝍔𝍕𝍖

░ ▒ ▓ █
◌
 ⃘
 ⃙
 ⃚
⦿ ◉ ⦾ ◌ ◎ ☉ ⃝ • ◦ 𝇇 𝇈 •⃢  ◦⃢  •⃣  ◦⃣
☀ ☼ ⚹ ⊕
☾☽ 🌑 🌒 🌓 🌔 🌕 🌖 🌗 🌘
 ̇ ̣ ᛫
•̣· ̣̇•

 ̣̇   ⃘

▤ ▥ ▦ ▧ ▨ ▩
⚄
x̣
⦾
*/


// code that generated the braille to bytes relationship.

//  val braille:immutable.TreeMap[Int, String] = immutable.TreeMap[Int, String](
//    0 -> "⠀", 0 + (1 << 4) -> "⠈", 0 + (2 << 4) -> "⠐", 0 + (3 << 4) -> "⠘", 0 + (4 << 4) -> "⠠", 0 + (5 << 4) -> "⠨", 0 + (6 << 4) -> "⠰", 0 + (7 << 4) -> "⠸", 0 + (8 << 4) -> "⢀", 0 + (9 << 4) -> "⢈", 0 + (10 << 4) -> "⢐", 0 + (11 << 4) -> "⢘", 0 + (12 << 4) -> "⢠", 0 + (13 << 4) -> "⢨", 0 + (14 << 4) -> "⢰", 0 + (15 << 4) -> "⢸",
//    1 -> "⠁", 1 + (1 << 4) -> "⠉", 1 + (2 << 4) -> "⠑", 1 + (3 << 4) -> "⠙", 1 + (4 << 4) -> "⠡", 1 + (5 << 4) -> "⠩", 1 + (6 << 4) -> "⠱", 1 + (7 << 4) -> "⠹", 1 + (8 << 4) -> "⢁", 1 + (9 << 4) -> "⢉", 1 + (10 << 4) -> "⢑", 1 + (11 << 4) -> "⢙", 1 + (12 << 4) -> "⢠", 1 + (13 << 4) -> "⢩", 1 + (14 << 4) -> "⢱", 1 + (15 << 4) -> "⢹",
//    2 -> "⠂", 2 + (1 << 4) -> "⠊", 2 + (2 << 4) -> "⠒", 2 + (3 << 4) -> "⠚", 2 + (4 << 4) -> "⠢", 2 + (5 << 4) -> "⠪", 2 + (6 << 4) -> "⠲", 2 + (7 << 4) -> "⠺", 2 + (8 << 4) -> "⢂", 2 + (9 << 4) -> "⢊", 2 + (10 << 4) -> "⢒", 2 + (11 << 4) -> "⢚", 2 + (12 << 4) -> "⢢", 2 + (13 << 4) -> "⢪", 2 + (14 << 4) -> "⢲", 2 + (15 << 4) -> "⢺",
//    3 -> "⠃", 3 + (1 << 4) -> "⠋", 3 + (2 << 4) -> "⠓", 3 + (3 << 4) -> "⠛", 3 + (4 << 4) -> "⠣", 3 + (5 << 4) -> "⠫", 3 + (6 << 4) -> "⠳", 3 + (7 << 4) -> "⠻", 3 + (8 << 4) -> "⢃", 3 + (9 << 4) -> "⢋", 3 + (10 << 4) -> "⢓", 3 + (11 << 4) -> "⢛", 3 + (12 << 4) -> "⢣", 3 + (13 << 4) -> "⢫", 3 + (14 << 4) -> "⢳", 3 + (15 << 4) -> "⢻",
//    4 -> "⠄", 4 + (1 << 4) -> "⠌", 4 + (2 << 4) -> "⠔", 4 + (3 << 4) -> "⠜", 4 + (4 << 4) -> "⠤", 4 + (5 << 4) -> "⠬", 4 + (6 << 4) -> "⠴", 4 + (7 << 4) -> "⠼", 4 + (8 << 4) -> "⢄", 4 + (9 << 4) -> "⢌", 4 + (10 << 4) -> "⢔", 4 + (11 << 4) -> "⢜", 4 + (12 << 4) -> "⢤", 4 + (13 << 4) -> "⢬", 4 + (14 << 4) -> "⢴", 4 + (15 << 4) -> "⢼",
//    5 -> "⠅", 5 + (1 << 4) -> "⠍", 5 + (2 << 4) -> "⠕", 5 + (3 << 4) -> "⠝", 5 + (4 << 4) -> "⠥", 5 + (5 << 4) -> "⠭", 5 + (6 << 4) -> "⠵", 5 + (7 << 4) -> "⠽", 5 + (8 << 4) -> "⢅", 5 + (9 << 4) -> "⢍", 5 + (10 << 4) -> "⢕", 5 + (11 << 4) -> "⢝", 5 + (12 << 4) -> "⢥", 5 + (13 << 4) -> "⢭", 5 + (14 << 4) -> "⢵", 5 + (15 << 4) -> "⢽",
//    6 -> "⠆", 6 + (1 << 4) -> "⠎", 6 + (2 << 4) -> "⠖", 6 + (3 << 4) -> "⠞", 6 + (4 << 4) -> "⠦", 6 + (5 << 4) -> "⠮", 6 + (6 << 4) -> "⠶", 6 + (7 << 4) -> "⠾", 6 + (8 << 4) -> "⢆", 6 + (9 << 4) -> "⢎", 6 + (10 << 4) -> "⢖", 6 + (11 << 4) -> "⢞", 6 + (12 << 4) -> "⢦", 6 + (13 << 4) -> "⢮", 6 + (14 << 4) -> "⢶", 6 + (15 << 4) -> "⢾",
//    7 -> "⠇", 7 + (1 << 4) -> "⠏", 7 + (2 << 4) -> "⠗", 7 + (3 << 4) -> "⠟", 7 + (4 << 4) -> "⠧", 7 + (5 << 4) -> "⠯", 7 + (6 << 4) -> "⠷", 7 + (7 << 4) -> "⠿", 7 + (8 << 4) -> "⢇", 7 + (9 << 4) -> "⢏", 7 + (10 << 4) -> "⢗", 7 + (11 << 4) -> "⢟", 7 + (12 << 4) -> "⢧", 7 + (13 << 4) -> "⢯", 7 + (14 << 4) -> "⢷", 7 + (15 << 4) -> "⢿",
//    8 -> "⡀", 8 + (1 << 4) -> "⡈", 8 + (2 << 4) -> "⡐", 8 + (3 << 4) -> "⡘", 8 + (4 << 4) -> "⡠", 8 + (5 << 4) -> "⡨", 8 + (6 << 4) -> "⡰", 8 + (7 << 4) -> "⡸", 8 + (8 << 4) -> "⣀", 8 + (9 << 4) -> "⣈", 8 + (10 << 4) -> "⣐", 8 + (11 << 4) -> "⣘", 8 + (12 << 4) -> "⣠", 8 + (13 << 4) -> "⣨", 8 + (14 << 4) -> "⣰", 8 + (15 << 4) -> "⣸",
//    9 -> "⡁", 9 + (1 << 4) -> "⡉", 9 + (2 << 4) -> "⡑", 9 + (3 << 4) -> "⡙", 9 + (4 << 4) -> "⡡", 9 + (5 << 4) -> "⡩", 9 + (6 << 4) -> "⡱", 9 + (7 << 4) -> "⡹", 9 + (8 << 4) -> "⣁", 9 + (9 << 4) -> "⣉", 9 + (10 << 4) -> "⣑", 9 + (11 << 4) -> "⣙", 9 + (12 << 4) -> "⣡", 9 + (13 << 4) -> "⣩", 9 + (14 << 4) -> "⣱", 9 + (15 << 4) -> "⣹",
//    10 -> "⡂", 10 + (1 << 4) -> "⡊", 10 + (2 << 4) -> "⡒", 10 + (3 << 4) -> "⡚", 10 + (4 << 4) -> "⡢", 10 + (5 << 4) -> "⡪", 10 + (6 << 4) -> "⡲", 10 + (7 << 4) -> "⡺", 10 + (8 << 4) -> "⣂", 10 + (9 << 4) -> "⣊", 10 + (10 << 4) -> "⣒", 10 + (11 << 4) -> "⣚", 10 + (12 << 4) -> "⣢", 10 + (13 << 4) -> "⣪", 10 + (14 << 4) -> "⣲", 10 + (15 << 4) -> "⣺",
//    11 -> "⡃", 11 + (1 << 4) -> "⡋", 11 + (2 << 4) -> "⡓", 11 + (3 << 4) -> "⡛", 11 + (4 << 4) -> "⡣", 11 + (5 << 4) -> "⡫", 11 + (6 << 4) -> "⡳", 11 + (7 << 4) -> "⡻", 11 + (8 << 4) -> "⣃", 11 + (9 << 4) -> "⣋", 11 + (10 << 4) -> "⣓", 11 + (11 << 4) -> "⣛", 11 + (12 << 4) -> "⣣", 11 + (13 << 4) -> "⣫", 11 + (14 << 4) -> "⣳", 11 + (15 << 4) -> "⣻",
//    12 -> "⡄", 12 + (1 << 4) -> "⡌", 12 + (2 << 4) -> "⡔", 12 + (3 << 4) -> "⡜", 12 + (4 << 4) -> "⡤", 12 + (5 << 4) -> "⡬", 12 + (6 << 4) -> "⡴", 12 + (7 << 4) -> "⡼", 12 + (8 << 4) -> "⣄", 12 + (9 << 4) -> "⣌", 12 + (10 << 4) -> "⣔", 12 + (11 << 4) -> "⣜", 12 + (12 << 4) -> "⣤", 12 + (13 << 4) -> "⣬", 12 + (14 << 4) -> "⣴", 12 + (15 << 4) -> "⣼",
//    13 -> "⡅", 13 + (1 << 4) -> "⡍", 13 + (2 << 4) -> "⡕", 13 + (3 << 4) -> "⡝", 13 + (4 << 4) -> "⡥", 13 + (5 << 4) -> "⡭", 13 + (6 << 4) -> "⡵", 13 + (7 << 4) -> "⡽", 13 + (8 << 4) -> "⣅", 13 + (9 << 4) -> "⣍", 13 + (10 << 4) -> "⣕", 13 + (11 << 4) -> "⣝", 13 + (12 << 4) -> "⣥", 13 + (13 << 4) -> "⣭", 13 + (14 << 4) -> "⣵", 13 + (15 << 4) -> "⣽",
//    14 -> "⡆", 14 + (1 << 4) -> "⡎", 14 + (2 << 4) -> "⡖", 14 + (3 << 4) -> "⡞", 14 + (4 << 4) -> "⡦", 14 + (5 << 4) -> "⡮", 14 + (6 << 4) -> "⡶", 14 + (7 << 4) -> "⡾", 14 + (8 << 4) -> "⣆", 14 + (9 << 4) -> "⣎", 14 + (10 << 4) -> "⣖", 14 + (11 << 4) -> "⣞", 14 + (12 << 4) -> "⣦", 14 + (13 << 4) -> "⣮", 14 + (14 << 4) -> "⣶", 14 + (15 << 4) -> "⣾",
//    15 -> "⡇", 15 + (1 << 4) -> "⡏", 15 + (2 << 4) -> "⡗", 15 + (3 << 4) -> "⡟", 15 + (4 << 4) -> "⡧", 15 + (5 << 4) -> "⡯", 15 + (6 << 4) -> "⡷", 15 + (7 << 4) -> "⡿", 15 + (8 << 4) -> "⣇", 15 + (9 << 4) -> "⣏", 15 + (10 << 4) -> "⣗", 15 + (11 << 4) -> "⣟", 15 + (12 << 4) -> "⣧", 15 + (13 << 4) -> "⣯", 15 + (14 << 4) -> "⣷", 15 + (15 << 4) -> "⣿"
//  )
//
//  for ((k, v) <- braille) print(s"\"$v\", ")

