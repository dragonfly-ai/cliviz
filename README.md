# cliviz
A Scala library for Command Line Interface Visualizations  inspired by <a href="https://github.com/JuliaPlots/UnicodePlots.jl">Julia Plots</a>.<br />
cliviz cross compiles to Scala.js, JVM, and Scala Native, so you can <a href="https://dragonfly-ai.github.io/cliviz/">try the demo</a> directly in the browser.

Features:
<ul>
<li>

Editable Command Line Image class: `CLImg`

This example features randomly generated and randomly colored spirals rendered to the command line output:<br />
<img src="https://raw.githubusercontent.com/dragonfly-ai/cliviz/refs/heads/main/screenshots/climg.png" alt="Unicode Spirals." />
</li>
<li>
Time Series Plots:<br />
<img src="https://raw.githubusercontent.com/dragonfly-ai/cliviz/refs/heads/main/screenshots/connectedScatter.png" alt="Unicode Spirals." />
</li>
<li>
Line Plots:<br />
<img src="https://raw.githubusercontent.com/dragonfly-ai/cliviz/refs/heads/main/screenshots/linePlot.png" alt="Unicode Line Plot." />
</li>
<li>
Scatter Plots:<br />
<img src="https://raw.githubusercontent.com/dragonfly-ai/cliviz/refs/heads/main/screenshots/scatterPlot.png" alt="Unicode Scatter Plot." />
</li>
<li>
Combined Pots (Scatter and line):<br />
<img src="https://raw.githubusercontent.com/dragonfly-ai/cliviz/refs/heads/main/screenshots/regressionPlot.png" alt="Unicode Regression Plot." />
</li>
<li>
Histogram plots:

```
[-17.00,-11.00 ) 🌑 ︙    ∝ 3.0E-4
[-11.00, -5.00 ) 🌑 ▕█   ∝ 0.0094
[ -5.00,  1.00 ) 🌑 ▕████████    ∝ 0.0732
[  1.00,  7.00 ) 🌒 ▕███████████████████████████    ∝ 0.2447
[  7.00, 13.00 ) 🌓 ▕███████████████████████████████████████   ∝ 0.3516
[ 13.00, 19.00 ) 🌔 ▕██████████████████████████▋   ∝ 0.2411`
[ 19.00, 25.00 ) 🌔 ▕███████▌   ∝ 0.069
[ 25.00, 31.00 ) 🌔 ▕█   ∝ 0.0101
[ 31.00, 37.00 ) 🌔 ︙    ∝ 5.0E-4
[ 37.00, 43.00 ] 🌕 ︰    ∝ 1.0E-4
```
The phases of the moon represent the cumulative distribution, while the labels on the left make use of standard mathematical interval notation: `()` for open intervals, `[]` for closed intervals, and `[)` for intervals closed at the low end and open at the high end.
</li>
</ul>

To use this library with SBT:

```scala
libraryDependencies += "ai.dragonfly" %%% "cliviz" % "<LATEST_VERSION>"
```
<br />

See a live demo here: https://dragonfly-ai.github.io/cliviz/