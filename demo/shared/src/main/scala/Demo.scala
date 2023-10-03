
import ai.dragonfly.democrossy.*

import ai.dragonfly.viz.cli.{CLImgDemo, ChartDemo}

/**
 * Created by clifton on 1/9/17.
 */

object Demo extends XApp(NativeConsole(style = "padding: 8px; overflow: scroll;")) with App {

    ChartDemo.demonstrate
    CLImgDemo.demonstrate

}
