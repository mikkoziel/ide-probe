package org.virtuslab.ideprobe.benchmark
package report

import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.duration._

class CSVBenchmarkReporter extends BenchmarkReporter {

  def report(suite: String, results: Seq[BenchmarkResult]): Unit = {
    val output = Paths.get(s"$suite.csv")

    val headers = Seq(
      "Benchmark name",
      "Num warmup runs",
      "Num measured runs",
      "Mean running time",
      "Median running time",
      "Sample standard deviation of running times",
      "Running times..."
    )

    def toSeconds(time: Duration): String = (time.toMillis / 1e3).toString

    def toSecondsOpt(time: Option[Duration]): String = time.fold("None")(toSeconds)

    val rows = results.map { result =>
      Seq(
        result.name,
        result.numberOfWarmups,
        result.numberOfRuns,
        toSecondsOpt(result.meanTime),
        toSecondsOpt(result.medianTime),
        toSecondsOpt(result.stdev)
      ) ++ result.results.map(toSeconds)
    }

    val content = (headers +: rows).map(_.mkString(",")).mkString("\n")

    val path = output.toAbsolutePath
    Files.createDirectories(path.getParent)
    Files.write(path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }
}
