# cliviz
A Scala library for Command Line Interface Visualizations  inspired by <a href="https://github.com/JuliaPlots/UnicodePlots.jl">Julia Plots</a>.<br />
cliviz cross compiles to Scala.js, JVM, and Scala Native, so you can <a href="https://dragonfly-ai.github.io/cliviz/">try the demo</a> directly in the browser.

Features:
<ul>
<li>

Editable Command Line Image class: `CLImg`

This example features randomly generated and randomly colored spirals rendered to the command line output:<br />
<img src="https://dragonfly-ai.github.io/cliviz/img/climg.png" />
</li>
<li>
Time Series Plots:<br />
<img src="https://dragonfly-ai.github.io/cliviz/img/connectedScatter.png" />
</li>
<li>
Line Plots:<br />
<img src="https://dragonfly-ai.github.io/cliviz/img/linePlot.png" />
</li>
<li>
Combined Pots (Scatter and line):<br />
<img src="https://dragonfly-ai.github.io/cliviz/img/regressionPlot.png" />
</li>

</ul>

To use this library with SBT:

```scala
libraryDependencies += "ai.dragonfly" %%% "cliviz" % "<LATEST_VERSION>"
```
<br />

See a live demo here: https://dragonfly-ai.github.io/cliviz/