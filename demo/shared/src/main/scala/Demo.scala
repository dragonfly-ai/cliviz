
import ai.dragonfly.math.*
import ai.dragonfly.math.stats.geometry.Tetrahedron
import ai.dragonfly.viz.cli.{CLImgDemo, ChartDemo}
import vector.*

/**
 * Created by clifton on 1/9/17.
 */

object Demo {

  import Console.{GREEN, RED, RESET, YELLOW, UNDERLINED, RED_B}

  lazy val consolidateDemoOutput: String = {
    implicit val sb: StringBuilder = new StringBuilder()
    sb.append(s"\n\n/* ${RESET}${GREEN}Begin ${CLImgDemo.name} Demonstration${RESET}*/\n")
    sb.append(s"${CLImgDemo.demo()}")
    sb.append(s"/* ${RESET}${RED}End ${CLImgDemo.name} Demonstration${RESET} */\n\n")

//    sb.append(s"\n\n/* ${RESET}${GREEN}Begin ${ChartDemo.name} Demonstration${RESET}*/\n")
//    sb.append(s"${ChartDemo.demo()}")
//    sb.append(s"/* ${RESET}${RED}End ${ChartDemo.name} Demonstration${RESET} */\n\n")

    sb.toString()
  }

  def main(args: Array[String]): Unit = {
    println(s"$RESET$GREEN$consolidateDemoOutput$RESET")
  }

}
