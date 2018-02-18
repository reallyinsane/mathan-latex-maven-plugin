/*
 * Copyright 2017 Matthias Hanisch
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

package io.mathan.latex.core;

public class Constants {

  /**
   * Identifier used as placeholder for the exeution of the latex tools to produce the output documment.
   */
  public static final String LaTeX = "LaTeX";
  public static final String FORMAT_DVI = "dvi";
  public static final String FORMAT_PDF = "pdf";
  public static final String FORMAT_PS = "ps";
  public static final String FORMAT_TEX = "tex";

  public static final String FORMAT_CLS = "cls";
  public static final String FORMAT_CLO = "clo";
  public static final String FORMAT_STY = "sty";
  public static final String FORMAT_BIB = "bib";
  public static final String FORMAT_BST = "bst";

  public static final String FORMAT_IDX = "idx";
  public static final String FORMAT_IST = "ist";
  public static final String FORMAT_GLO = "glo";
  public static final String FORMAT_EPS = "eps";
  public static final String FORMAT_AUX = "aux";
  public static final String FORMAT_BBL = "bbl";
  public static final String FORMAT_BCF = "bcf";
  public static final String FORMAT_NLO = "nlo";
  public static final String FORMAT_NLS = "nls";

  public static final String[] RESOURCES_DEFAULT_EXTENSTIONS = {
      Constants.FORMAT_TEX, Constants.FORMAT_CLS, Constants.FORMAT_CLO, Constants.FORMAT_STY,
      Constants.FORMAT_BIB, Constants.FORMAT_BST, Constants.FORMAT_IDX, Constants.FORMAT_IST,
      Constants.FORMAT_GLO, Constants.FORMAT_EPS, Constants.FORMAT_PDF};
}
