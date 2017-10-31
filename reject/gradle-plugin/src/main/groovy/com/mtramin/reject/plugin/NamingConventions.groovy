package com.mtramin.reject.plugin

import org.gradle.api.tasks.SourceSet

/**
 * Taken from: https://github.com/eveoh/gradle-aspectj
 */
public interface NamingConventions {

    String getJavaCompileTaskName(SourceSet sourceSet);

    String getAspectCompileTaskName(SourceSet sourceSet);

    String getAspectPathConfigurationName(SourceSet sourceSet);

    String getAspectInpathConfigurationName(SourceSet sourceSet);
}
