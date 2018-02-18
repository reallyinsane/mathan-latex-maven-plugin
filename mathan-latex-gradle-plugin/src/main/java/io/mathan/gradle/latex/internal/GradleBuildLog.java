/*
 * Copyright 2018 Matthias Hanisch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mathan.gradle.latex.internal;

import io.mathan.latex.core.BuildLog;
import org.gradle.api.logging.Logger;

public class GradleBuildLog implements BuildLog {

  private final Logger logger;

  GradleBuildLog(Logger logger) {
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
