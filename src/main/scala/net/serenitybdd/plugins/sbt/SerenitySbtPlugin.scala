package com.tysafe.sbt.serenity

import net.serenitybdd.plugins.sbt.SerenityPluginExtension
import sbt.Keys._
import sbt.Tests.Cleanup
import sbt._
import plugins._

object SerenitySbtPlugin extends AutoPlugin with SerenityPluginExtension {

  override def projectKey = Def.setting(name.value).toString

  object autoImport {
    val serenityReportTask = taskKey[Unit]("Serenity sbt report task")
    val clearReports =
      taskKey[Unit]("Serenity sbt task to delete report directory")
    val historyReports = taskKey[Unit]("Serenity sbt task to create history")
    val clearHistory = taskKey[Unit]("Serenity sbt task to delete history")
  }

  import autoImport._

  override def trigger: PluginTrigger = allRequirements

  override def requires: AutoPlugin = JvmPlugin

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    test := {
      serenityReportTask.dependsOn((Test / test).result).value
    },
    testOnly := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        serenityReportTask.dependsOn(
          (Test / testOnly).toTask(" " + args.mkString(" ")).result
        )
      }
    }).evaluated,
    testQuick := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        serenityReportTask.dependsOn(
          (Test / testQuick).toTask(" " + args.mkString(" ")).result
        )
      }
    }).evaluated,
    clean := {
      clearReports.dependsOn((clean).result).value
    },
    clearReports := {
      System.setProperty(
        "project.build.directory",
        baseDirectory.value.getAbsolutePath
      )
      println("cleaning serenity report directory.")
      clearReportFiles()
    },
    clearHistory := {
      System.setProperty(
        "project.build.directory",
        baseDirectory.value.getAbsolutePath
      )
      println("cleaning serenity report history.")
      clearHistoryFiles()
    },
    historyReports := {
      System.setProperty(
        "project.build.directory",
        baseDirectory.value.getAbsolutePath
      )
      println("generating Serenity report history.")
      generateHistory()
    },
    serenityReportTask := {
      System.setProperty(
        "project.build.directory",
        baseDirectory.value.getAbsolutePath
      )
      println("generating Serenity report.")
      execute()
    }
  )

}
