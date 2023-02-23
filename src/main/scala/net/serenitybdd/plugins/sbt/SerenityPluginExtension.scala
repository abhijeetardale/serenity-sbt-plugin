package net.serenitybdd.plugins.sbt

import java.io.IOException
import java.nio.file.Files
import java.util
import java.util.stream.Collectors
import java.util.{Optional, Locale}

import net.serenitybdd.core.Serenity
import net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.UserStoryTestReporter
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.webdriver.Configuration
import org.apache.commons.io.FileUtils
import sbt.Keys._;
import scala.reflect.runtime.{universe => ru}

trait SerenityPluginExtension {
  def environmentVariables =
    Injectors.getInjector.getProvider(classOf[EnvironmentVariables]).get
  def systemPropertiesConfiguration = Injectors.getInjector
    .getProvider(classOf[SystemPropertiesConfiguration])
    .get

  lazy val configuration =
    Injectors
      .getInjector()
      .getProvider(classOf[Configuration[Configuration[_]]])
      .get()
  lazy val outputDirectory = configuration.getOutputDirectory()
  lazy val historyDirectory = configuration.getHistoryDirectory()
  lazy val sourceDirectory = outputDirectory

  lazy val issueTrackerUrl = environmentVariables.getProperty("jira.url")
  lazy val jiraUrl = environmentVariables.getProperty("jira.url")
  lazy val jiraUsername = environmentVariables.getProperty("jira.username")
  lazy val jiraPassword = environmentVariables.getProperty("jira.password")
  lazy val jiraProject = environmentVariables.getProperty("jira.project")
  lazy val tags = environmentVariables.getProperty("cucumber.options.tags")

  lazy val DEFAULT_HISTORY_DIRECTORY: String =
    environmentVariables.getProperty("history", "history")

  lazy val requirementBaseDir: String = environmentVariables.getProperty(
    ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR.getPropertyName
  )
  lazy val generateOutcomes: Boolean = true

  def projectKey = Serenity.getDefaultProjectKey

  def configureEnvironmentVariables() = {
    Locale.setDefault(Locale.ENGLISH)
    updateSystemProperty(
      ThucydidesSystemProperty.SERENITY_PROJECT_KEY.getPropertyName,
      projectKey,
      Serenity.getDefaultProjectKey
    )
    updateSystemProperty(
      ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR.getPropertyName,
      requirementBaseDir,
      environmentVariables.getProperty("user.dir")
    )
    systemPropertiesConfiguration.reloadOutputDirectory()
  }

  private def updateSystemProperty(
      key: String,
      value: String,
      defaultValue: String
  ) {
    environmentVariables.setProperty(
      key,
      Optional.ofNullable(value).orElse(defaultValue)
    )
  }

  @throws(classOf[IOException])
  private def generateHtmlStoryReports() = {

    val reporter = new HtmlAggregateStoryReporter(projectKey);

    reporter.setSourceDirectory(sourceDirectory)
    reporter.setOutputDirectory(outputDirectory)
    reporter.setIssueTrackerUrl(issueTrackerUrl)
    reporter.setJiraUrl(jiraUrl)
    reporter.setJiraProject(jiraProject)
    reporter.setJiraUsername(jiraUsername)
    reporter.setJiraPassword(jiraPassword)
    // reporter
    // reporter.setTags(tags)
    if (generateOutcomes) {
      reporter.setGenerateTestOutcomeReports
    }
    reporter.generateReportsForTestResultsFrom(sourceDirectory)
  }

  private def generateCustomReports() = {

    val customReporters = getCustomReportsFor(environmentVariables)

    for (reporter <- customReporters) {
      reporter.generateReportsForTestResultsFrom(outputDirectory)
    }

  }

  private def getCustomReportsFor(
      environmentVariables: EnvironmentVariables
  ): List[UserStoryTestReporter] = {

    val allKeys = environmentVariables
      .getKeys()
      .asInstanceOf[util.ArrayList[String]]
      .toArray
      .foldLeft(List.empty[String]) { (a, b) =>
        if (b.ne(Nil) && b.isInstanceOf[String]) {
          a ++ List(b.asInstanceOf[String])
        } else a
      }

    val keys =
      allKeys.filter(key => key.startsWith("serenity.custom.reporters."))

    keys.map(key => reportFrom(key)).filter(_.isDefined).map(_.get)

  }

  private def reportFrom(key: String): Option[UserStoryTestReporter] = {
    val reportClass = environmentVariables.getProperty(key)
    try {
      val action = Class.forName(reportClass).getConstructor().newInstance()
      Some(action.asInstanceOf[UserStoryTestReporter])
    } catch {
      case e: Any => {
        None
      }
    }
  }

  def execute() = {

    if (historyDirectory.exists) {
      clearHistoryFiles()
    }

    configureEnvironmentVariables()

    try {
      generateHtmlStoryReports()
      generateCustomReports()
    } catch {
      case e: IOException => {
        throw new IOException("Error generating aggregate serenity reports", e)
      }
    }
  }

  def clearReportFiles() = {

    configureEnvironmentVariables()

    try {
      FileUtils.deleteDirectory(FileUtils.getFile(outputDirectory.getParent))
      // FileUtils.deleteDirectory(outputDirectory)
    } catch {
      case e: IOException => {
        throw new IOException("Error deleting serenity reports history", e)
      }
    }
  }

  @throws(classOf[IOException])
  def clearHistoryFiles() = {

    configureEnvironmentVariables()

    try {
      FileUtils.deleteDirectory(historyDirectory)
    } catch {
      case e: IOException => {
        throw new IOException("Error deleting serenity reports history", e)
      }
    }
  }

  @throws(classOf[IOException])
  def generateHistory() = {

    if (!historyDirectory.exists) {
      historyDirectory.mkdirs
    }

    configureEnvironmentVariables()

    try {
      FileUtils.deleteDirectory(historyDirectory)
      new FileSystemTestOutcomeSummaryRecorder(
        historyDirectory.toPath,
        deletePreviousHistory()
      )
        .recordOutcomeSummariesFrom(sourceDirectory.toPath);
    } catch {
      case e: IOException => {
        throw new IOException("Error generating serenity reports history", e)
      }
    }
  }

  def deletePreviousHistory(): Boolean = {
    environmentVariables.getPropertyAsBoolean(
      ThucydidesSystemProperty.DELETE_HISTORY_DIRECTORY,
      true
    )
  }

}
