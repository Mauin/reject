package com.mtramin.reject.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskInstantiationException
import org.gradle.api.tasks.compile.JavaCompile
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

/**
 * Java implementation details from: https://github.com/eveoh/gradle-aspectj
 */
class RejectPlugin implements Plugin<Project> {

  def ASPECTJ_VERSION = "1.8.12"

  void apply(Project project) {
    project.dependencies {
      compile "com.mtramin.reject:annotation:1.0.0"
      compile "com.mtramin.reject:aspect:1.0.0"
    }

    switch (getPluginType(project)) {
      case PluginType.ANDROID:
        configureForAndroid(project)
        break
      case PluginType.JAVA:
        configureForJava(project)
        break
      default:
        throw new TaskInstantiationException("No supported Plugins found.")
    }
  }

  private PluginType getPluginType(Project project) {
    println(project.plugins)
    def androidAppPlugin = project.plugins.hasPlugin("com.android.application")
    def androidLibraryPlugin = project.plugins.hasPlugin("com.android.library")
    def javaPlugin = project.plugins.hasPlugin("java")
    def javaLibraryPlugin = project.plugins.hasPlugin("java-library")

    if (androidAppPlugin || androidLibraryPlugin) {
      return PluginType.ANDROID
    } else if (javaLibraryPlugin || javaPlugin) {
      return PluginType.JAVA
    } else {
      throw TaskInstantiationException("No supported Gradle Plugin detected.");
    }
  }

  private enum PluginType {
    ANDROID, JAVA
  }

  private void configureForAndroid(Project project) {
    println "Android"
    def variants = getAndroidVariants(project)
    variants.all { variant ->
      JavaCompile javaCompile = variant.javaCompile
      javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.5",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]

        final MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler);
        for (IMessage message : handler.getMessages(null, true)) {
          switch (message.getKind()) {
            case IMessage.ABORT:
            case IMessage.ERROR:
            case IMessage.FAIL:
//              log.error message.message, message.thrown
              break;
            case IMessage.WARNING:
//              log.warn message.message, message.thrown
              break;
            case IMessage.INFO:
//              log.info message.message, message.thrown
              break;
            case IMessage.DEBUG:
//              log.debug message.message, message.thrown
              break;
          }
        }
      }
    }
  }

  def getAndroidVariants(Project project) {
    println("Java")
    def variants
    if (project.plugins.hasPlugin("com.android.application")) {
      variants = project.android.applicationVariants
    } else {
      variants = project.android.libraryVariants
    }
    return variants
  }

  private void configureForJava(Project project) {
    if (project.configurations.findByName('ajtools') == null) {
      project.configurations.create('ajtools')
      project.dependencies {
        ajtools "org.aspectj:aspectjtools:${ASPECTJ_VERSION}"
        compile "org.aspectj:aspectjrt:${ASPECTJ_VERSION}"
      }
    }

    for (projectSourceSet in project.sourceSets) {
      def namingConventions = projectSourceSet.name.equals('main') ? new MainNamingConventions() : new DefaultNamingConventions();
      for (configuration in [namingConventions.getAspectPathConfigurationName(projectSourceSet), namingConventions.getAspectInpathConfigurationName(projectSourceSet)]) {
        if (project.configurations.findByName(configuration) == null) {
          project.configurations.create(configuration)
        }
      }

      if (!projectSourceSet.allJava.isEmpty()) {
        def aspectTaskName = namingConventions.getAspectCompileTaskName(projectSourceSet)
        def javaTaskName = namingConventions.getJavaCompileTaskName(projectSourceSet)

        project.tasks.create(name: aspectTaskName, overwrite: true, description: "Compiles AspectJ Source for ${projectSourceSet.name} source set", type: Ajc) {
          sourceSet = projectSourceSet
          inputs.files(sourceSet.allJava)
          outputs.dir(sourceSet.java.outputDir)
          aspectpath = project.configurations.findByName(namingConventions.getAspectPathConfigurationName(projectSourceSet))
          ajInpath = project.configurations.findByName(namingConventions.getAspectInpathConfigurationName(projectSourceSet))
        }

        project.tasks[aspectTaskName].setDependsOn(project.tasks[javaTaskName].dependsOn)
        project.tasks[aspectTaskName].dependsOn(project.tasks[aspectTaskName].aspectpath)
        project.tasks[aspectTaskName].dependsOn(project.tasks[aspectTaskName].ajInpath)
        project.tasks[javaTaskName].deleteAllActions()
        project.tasks[javaTaskName].dependsOn(project.tasks[aspectTaskName])
      }
    }

    project.dependencies {
      aspectpath "com.mtramin.reject:aspect:1.0.0"
    }
  }

  private static class MainNamingConventions implements NamingConventions {

    @Override
    String getJavaCompileTaskName(final SourceSet sourceSet) {
      return "compileJava"
    }

    @Override
    String getAspectCompileTaskName(final SourceSet sourceSet) {
      return "compileAspect"
    }

    @Override
    String getAspectPathConfigurationName(final SourceSet sourceSet) {
      return "aspectpath"
    }

    @Override
    String getAspectInpathConfigurationName(final SourceSet sourceSet) {
      return "ajInpath"
    }
  }

  private static class DefaultNamingConventions implements NamingConventions {

    @Override
    String getJavaCompileTaskName(final SourceSet sourceSet) {
      return "compile${sourceSet.name.capitalize()}Java"
    }

    @Override
    String getAspectCompileTaskName(final SourceSet sourceSet) {
      return "compile${sourceSet.name.capitalize()}Aspect"
    }

    @Override
    String getAspectPathConfigurationName(final SourceSet sourceSet) {
      return "${sourceSet.name}Aspectpath"
    }

    @Override
    String getAspectInpathConfigurationName(final SourceSet sourceSet) {
      return "${sourceSet.name}AjInpath"
    }
  }
}
