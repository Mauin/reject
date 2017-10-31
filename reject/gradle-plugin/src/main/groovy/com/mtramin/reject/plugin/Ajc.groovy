package com.mtramin.reject.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

/**
 * Taken from: https://github.com/eveoh/gradle-aspectj
 */
class Ajc extends DefaultTask {

  SourceSet sourceSet

  FileCollection aspectpath
  FileCollection ajInpath

  // ignore or warning
  String xlint = 'ignore'

  String maxmem
  Map<String, String> additionalAjcArgs

  Ajc() {
    logging.captureStandardOutput(LogLevel.INFO)
  }

  @TaskAction
  def compile() {
    logger.info("=" * 30)
    logger.info("=" * 30)
    logger.info("Running ajc ...")
    logger.info("classpath: ${sourceSet.compileClasspath.asPath}")
    logger.info("srcDirs $sourceSet.java.srcDirs")

    def iajcArgs = [classpath           : sourceSet.compileClasspath.asPath,
                    destDir             : sourceSet.java.outputDir.absolutePath,
                    s                   : sourceSet.java.outputDir.absolutePath,
                    source              : project.convention.plugins.java.sourceCompatibility,
                    target              : project.convention.plugins.java.targetCompatibility,
                    inpath              : ajInpath.asPath,
                    xlint               : xlint,
                    fork                : 'true',
                    aspectPath          : aspectpath.asPath,
                    sourceRootCopyFilter: '**/*.java,**/*.aj',
                    showWeaveInfo       : 'true']

    if (null != maxmem) {
      iajcArgs['maxmem'] = maxmem
    }

    if (null != additionalAjcArgs) {
      for (pair in additionalAjcArgs) {
        iajcArgs[pair.key] = pair.value
      }
    }

    ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties", classpath: project.configurations.ajtools.asPath)
    ant.iajc(iajcArgs) {
      sourceRoots {
        sourceSet.java.srcDirs.each {
          logger.info("   sourceRoot $it")
          pathelement(location: it.absolutePath)
        }
      }
    }
  }
}
