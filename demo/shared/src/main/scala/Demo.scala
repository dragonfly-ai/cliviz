
import ai.dragonfly.democrossy.*

import ai.dragonfly.viz.cli.{CLImgDemo, ChartDemo}

import Console.*

/**
 * Created by clifton on 1/9/17.
 */

object Demo extends XApp(DivConsole(style = "padding: 8px; overflow: scroll;")) with App {

    CLImgDemo.demonstrate
    ChartDemo.demonstrate

}
