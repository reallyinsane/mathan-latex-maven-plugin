package io.mathan.gradle.latex.internal;

import io.mathan.latex.core.BuildLog;
import org.gradle.api.logging.Logger;

public class GradleBuildLog implements BuildLog {

  private final Logger logger;

  public GradleBuildLog(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void error(String message) {
    this.logger.error(message);
  }

  @Override
  public void info(String message) {
    this.logger.info(message);
  }

  @Override
  public void warn(String message) {
    this.logger.warn(message);
  }

  @Override
  public void error(String message, Exception ex) {
    this.logger.error(message, ex);
  }

  @Override
  public void info(String message, Exception ex) {
    this.logger.info(message, ex);
  }

  @Override
  public void warn(String message, Exception ex) {
    this.logger.warn(message, ex);
  }
}
