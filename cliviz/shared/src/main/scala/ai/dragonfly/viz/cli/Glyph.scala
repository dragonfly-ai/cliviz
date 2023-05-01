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

import scala.Console.RESET

object Glyph {

  val `⠀ͯ`:String = "ͯ"
  //val `⠀⃘`:String = "⃘"
  val `⠀⃟`:String = "⃟"
  val `⠀⃞`:String = "⃞"
  val `⠀ ⃤`:String = "⃤"
  //val `⠀⃠`:String = "⃠"
  val `⠀⃧`:String = "⃧"

  val `⠀᷎`:String = "᷎"
  val `⠀᷀`:String = "᷀"
  val `⠀ᷘ`:String = "ᷘ"
  val `⠀͒`:String = "͒"
  val `͂ `:String = "͂"
  val `᷃ `:String = "᷃"

  val `⠀᷾`:String = "᷾"
  val `⠀͐`:String = "͐"
  val `⠀̀`:String = "̀"
  val `⠀́`:String = "́"
  val `⠀ͣ`:String = "ͣ"
  val `⠀͚`:String = "͚"
  val `⠀ͦ`:String = "ͦ"
  val `⠀ͨ`:String = "ͨ"

  //    ⃘⃟⃞⃤⃠  <-- See?  They draw on top of each other.
  private val layerGlyphs:Array[String] = Array[String](
    // `⠀⃘`, `⠀⃠`,
    `⠀ͯ`, `⠀⃟`, `⠀⃞`, `⠀ ⃤`, `⠀⃧`, `⠀᷎`, `⠀᷀`, `⠀ᷘ`, `⠀͒`, `͂ `, `᷃ `, `⠀᷾`, `⠀͐`, `⠀̀`, `⠀́`, `⠀ͣ`, `⠀͚`, `⠀ͦ`, `⠀ͨ`
  )


  def apply(strId:Int):Glyph = Glyph(strId, 1 << (strId % 3), strId > 2)

  def apply(strId:Int, color:Int):Glyph = Glyph( strId, color, strId > 3 )

  val axis:Glyph = Glyph(-1, CLImg.WHITE, true)  // "͙"
}

case class Glyph(id:Int, color:Int, overlay:Boolean) {
  override def toString:String = id match {
    case -1 => "ͯ"
    case pixel:Int if 0 <= pixel && pixel < 3 => "⠒"
    case _ => Glyph.layerGlyphs(id - 3)
  }

  def forceColor: String = s"${CLImg.colorBytes(color)}$toString"

  def asIcon:String = {
    if (overlay) s"${CLImg.colorBytes(color)}⠀⠀$toString⠀$RESET"
    else s"⠀${CLImg.colorBytes(color)}$toString⠀$RESET"
  }
}

/*
⣿̀ 	⣿́ 	⣿̂ 	⣿̃ 	⣿̄ 	⣿̅ 	⣿̆ 	⣿̇ 	⣿̈ 	⣿̉ 	⣿̊ 	⣿̋ 	⣿̌ 	⣿̍ 	⣿̎ 	⣿̏
⣿̐ 	⣿̑ 	⣿̒ 	⣿̓ 	⣿̔ 	⣿̕ 	⣿̖ 	⣿̗ 	⣿̘ 	⣿̙ 	⣿̚ 	⣿̛ 	⣿̜ 	⣿̝ 	⣿̞ 	⣿̟
⣿̠ 	⣿̡ 	⣿̢ 	⣿̣ 	⣿̤ 	⣿̥ 	⣿̦ 	⣿̧ 	⣿̨ 	⣿̩ 	⣿̪ 	⣿̫ 	⣿̬ 	⣿̭ 	⣿̮ 	⣿̯
⣿̰ 	⣿̱ 	⣿̲ 	⣿̳ 	⣿̴ 	⣿̵ 	⣿̶ 	⣿̷ 	⣿̸ 	⣿̹ 	⣿̺ 	⣿̻ 	⣿̼ 	⣿̽ 	⣿̾ 	⣿̿
⣿̀ 	⣿́ 	⣿͂ 	⣿̓ 	⣿̈́ 	⣿ͅ 	⣿͆ 	⣿͇ 	⣿͈ 	⣿͉ 	⣿͊ 	⣿͋ 	⣿͌ 	⣿͍ 	⣿͎
⣿͐ 	⣿͑ 	⣿͒ 	⣿͓ 	⣿͔ 	⣿͕ 	⣿͖ 	⣿͗ 	⣿͘ 	⣿͙ 	⣿͚ 	⣿͛ 	⣿͜⣿ 	⣿͝⣿ 	⣿͞⣿ 	⣿͟⣿̅
 	⣿͠⣿ 	⣿͡⣿ 	⣿͢⣿ 	⣿ͣ 	⣿ͤ 	⣿ͥ 	⣿ͦ 	⣿ͧ 	⣿ͨ 	⣿ͩ 	⣿ͪ 	⣿ͫ 	⣿ͬ 	⣿ͭ
⣿᷀ 	⣿᷁ 	⣿᷂ 	⣿᷃ 	⣿᷄ 	⣿᷅ 	⣿᷆ 	⣿᷇ 	⣿᷈ 	⣿᷉ 	⣿᷊ 	⣿᷋ 	⣿᷌ 	⣿᷍ 	⣿᷎ 	⣿᷏
⣿⃐ 	⣿⃑ 	⣿⃒ 	⣿⃓ 	⣿⃔ 	⣿⃕ 	⣿⃖ 	⣿⃗ 	⣿⃘ 	⣿⃙ 	⣿⃚ 	⣿⃛ 	⣿⃜ 	⣿⃝ 	⣿⃞ 	⣿⃟
⣿⃠ 	⣿⃡ 	⣿⃢ 	⣿⃣ 	⣿⃤ 	⣿⃥ 	⣿⃦ 	⣿⃧ 	⣿ 	⣿⃩ 	⣿⃪ 	⣿⃫ 	⣿⃬ 	⣿⃭ 	⣿⃮ 	⣿⃯
⣿⃰ 	⣿ᷓ 	⣿ᷔ 	⣿ᷕ 	⣿ᷖ 	⣿ᷗ 	⣿ᷘ 	⣿ᷙ 	⣿ᷚ 	⣿ᷛ 	⣿ᷜ 	⣿ᷝ 	⣿ᷞ 	⣿ᷟ  	⣿ͮ 	⣿ͯ
⣿ᷠ 	⣿ᷡ 	⣿ᷢ 	⣿ᷣ 	⣿ᷤ 	⣿ᷥ 	⣿ᷦ 	⣿᷼ 	⣿᷽ 	⣿᷾ 	⣿᷿
⣿︠ 	⣿︡ 	⣿︢ 	⣿︣ 	⣿︤ 	⣿︥ 	⣿︦

Combining Char᷍acters:
⠀⃐ 	⠀⃑ 	⠀⃒ 	⠀⃓ 	⠀⃔ 	⠀⃕ 	⠀⃖ 	⠀⃗ 	⠀⃘ 	⠀⃙ 	⠀⃚ 	⠀⃛ 	⠀⃜ 	⠀⃝ 	⠀⃞ 	⠀⃟
⠀⃠ 	⠀⃡ 	⠀⃢ 	⠀⃣ 	⠀⃤ 	⠀⃥ 	⠀⃦ 	⠀⃧ 	⠀⃨ 	⠀⃩ 	⠀⃪ 	⠀⃫ 	⠀⃬ 	⠀⃭ 	⠀⃮ 	⠀⃯
⠀⃰
*/