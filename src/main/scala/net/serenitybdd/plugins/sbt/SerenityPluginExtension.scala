package net.serenitybdd.plugins.sbt

import java.io.IOException
import java.util.stream.Collectors
import java.util.{Optional, Locale}

import net.serenitybdd.core.Serenity
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.UserStoryTestReporter
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.webdriver.Configuration
import sbt.Keys._
;
import scala.reflect.runtime.{ universe => ru }


trait SerenityPluginExtension {
  val environmentVariables = Injectors.getInjector.getProvider(classOf[EnvironmentVariables]).get
  val systemPropertiesConfiguration = Injectors.getInjector.getProvider(classOf[SystemPropertiesConfiguration]).get


  def configuration = Injectors.getInjector().getProvider(classOf[Configuration]).get()
  def outputDirectory = configuration.getOutputDirectory()
  def historyDirectory = configuration.getHistoryDirectory()
  def sourceDirectory = outputDirectory

  val reporter = new HtmlAggregateStoryReporter(projectKey);

  def projectKey = Serenity.getDefaultProjectKey

  def configureEnvironmentVariables() = {
    Locale.setDefault(Locale.ENGLISH)
    updateSystemProperty(ThucydidesSystemProperty.THUCYDIDES_PROJECT_KEY.getPropertyName, projectKey, Serenity.getDefaultProjectKey)
    //updateSystemProperty(ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR..getPropertyName, configuration.getRe)
    systemPropertiesConfiguration.reloadOutputDirectory()
  }

  private def updateSystemProperty(key: String, value: String, defaultValue: String) {
    environmentVariables.setProperty(key, Optional.ofNullable(value).orElse(defaultValue))
  }

  @throws(classOf[IOException])
  private def generateHtmlStoryReports() =  {
    reporter.setSourceDirectory(sourceDirectory)
    reporter.setOutputDirectory(outputDirectory)
    //reporter.setIssueTrackerUrl(issueTrackerUrl)
    //reporter.setJiraUrl(jiraUrl)
    //reporter.setJiraProject(jiraProject)
    //reporter.setJiraUsername(jiraUsername)
    //reporter.setJiraPassword(jiraPassword)
    //reporter.setTags(tags)
    // if (generateOutcomes) {
    reporter.setGenerateTestOutcomeReports
    // }
    reporter.generateReportsForTestResultsFrom(sourceDirectory)
  }



  @throws(classOf[IOException])
  def execute() =  {

    if (!outputDirectory.exists) {
      outputDirectory.mkdirs
    }

    configureEnvironmentVariables()

    try {
      generateHtmlStoryReports()
    }
    catch {
      case e: IOException => {
        throw new IOException("Error generating aggregate serenity reports", e)
      }
    }

  }
}