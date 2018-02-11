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

package io.mathan.maven.latex.internal;

import io.mathan.latex.core.BuildLog;
import org.apache.maven.plugin.logging.Log;

public class MavenBuildLog implements BuildLog {

  private Log log;

  public MavenBuildLog(Log log) {

    this.log = log;
  }

  @Override
  public void error(String message) {
    this.log.error(message);
  }

  @Override
  public void info(String message) {
    this.log.info(message);
  }

  @Override
  public void warn(String message) {
    this.log.warn(message);
  }

  @Override
  public void error(String message, Exception ex) {
    this.log.error(message, ex);
  }

  @Override
  public void info(String message, Exception ex) {
    this.log.info(message, ex);
  }

  @Override
  public void warn(String message, Exception ex) {
    this.log.warn(message, ex);
  }
}
