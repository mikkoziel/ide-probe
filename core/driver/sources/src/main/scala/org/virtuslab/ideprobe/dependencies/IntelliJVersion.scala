package org.virtuslab.ideprobe.dependencies

import org.virtuslab.ideprobe.BuildInfo
import pureconfig.ConfigConvert
import pureconfig.generic.semiauto.deriveConvert

final case class IntelliJVersion(build: String, release: Option[String]) {
  def major: Option[String] = {
    release.map(_.split("\\.").take(2).mkString("."))
  }

  def inferredMajor: String = {
    major.getOrElse {
      val firstDigits = build.split('.')(0)
      val year = firstDigits.take(2)
      val version = firstDigits.drop(2)
      s"20$year.$version"
    }
  }

  def compatibleScalaVersion: String =
    if (inferredMajor.toDouble < 2020.3) "2.12" else "2.13"

  override def toString: String = {
    val version = release.fold(build)(r => s"$r, $build")
    s"IntelliJ($version)"
  }
}

object IntelliJVersion {
  implicit val configConvert: ConfigConvert[IntelliJVersion] = deriveConvert[IntelliJVersion]

  val Latest = snapshot("212.4416.7")

  def snapshot(build: String): IntelliJVersion = {
    IntelliJVersion(build, None)
  }

  def release(version: String, build: String): IntelliJVersion = {
    IntelliJVersion(build, Some(version))
  }
}
