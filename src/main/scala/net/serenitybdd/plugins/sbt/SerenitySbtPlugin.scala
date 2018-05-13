package com.tysafe.sbt.serenity

import net.serenitybdd.plugins.sbt.SerenityPluginExtension
import sbt.Keys._
import sbt._
import plugins._

object SerenitySbtPlugin extends AutoPlugin with SerenityPluginExtension {

  override def projectKey = Def.setting(name.value).toString

  object autoImport {
    val serenityReportTask = taskKey[Unit]("Serenity sbt report task")
  }

  import autoImport._

  override def trigger: PluginTrigger = allRequirements

  override def requires : AutoPlugin = JvmPlugin

  override lazy val projectSettings : Seq[Setting[_]] = Seq(

    test := {
      serenityReportTask.dependsOn((test in Test).result).value
    },

    testOnly := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        serenityReportTask.dependsOn((testOnly in Test).toTask(" " + args.mkString(" ")).result)
      }
    }).evaluated,

    testQuick := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        serenityReportTask.dependsOn((testQuick in Test).toTask(" " + args.mkString(" ")).result)
      }
    }).evaluated,

    serenityReportTask := {
      System.setProperty("project.build.directory", baseDirectory.value.getAbsolutePath)
      println("Generating Serenity report.")
      execute()
    }

  )

}