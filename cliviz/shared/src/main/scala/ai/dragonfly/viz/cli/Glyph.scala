package ai.dragonfly.viz.cli

import scala.Console.RESET

object Glyph {

  val `⠀ͯ`:String = "ͯ"
  val `⠀⃘`:String = "⃘"
  val `⠀⃟`:String = "⃟"
  val `⠀⃞`:String =  "⃞"
  val `⠀ ⃤`:String =  "⃤"
  val `⠀⃠`:String = "⃠"
  val `⠀⃧`:String = "⃧"

  val `⠀᷎`:String = " ᷎"
  val `⠀᷀`:String = " ᷀"
  val `⠀ᷘ`:String = " ᷘ"
  val `⠀͒`:String = " ͒"
  val `͂ `:String = "͂ "
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
    `⠀ͯ`, `⠀⃘`, `⠀⃟`, `⠀⃞`, `⠀ ⃤`, `⠀⃠`, `⠀⃧`, `⠀᷎`, `⠀᷀`, `⠀ᷘ`, `⠀͒`, `͂ `, `᷃ `, `⠀᷾`, `⠀͐`, `⠀̀`, `⠀́`, `⠀ͣ`, `⠀͚`, `⠀ͦ`, `⠀ͨ`
  )


  def apply(strId:Int):Glyph = Glyph(strId, 1 << (strId % 3), strId > 2)

  def apply(strId:Int, color:Int):Glyph = Glyph( strId, color, strId > 3 )

  val axis:Glyph = Glyph(-1, CLImg.GRAY, true)  // "͙"
}

case class Glyph(id:Int, color:Int, overlay:Boolean) {

  val str:String = id match {
    case -1 => "ͯ"
    case pixel:Int if 0 <= pixel && pixel < 3 => "⠐"
    case _ => Glyph.layerGlyphs(id - 3)
  }

  override def toString: String = {
    if (overlay) s"${CLImg.colorBytes(color)} $str $RESET"
    else s"${CLImg.colorBytes(color)}$str $RESET"
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