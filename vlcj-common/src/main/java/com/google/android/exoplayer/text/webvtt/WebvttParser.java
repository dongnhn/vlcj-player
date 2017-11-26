/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer.text.webvtt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.TextUtils;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.util.MimeTypes;

/**
 * A simple WebVTT parser.
 * <p>
 * @see <a href="http://dev.w3.org/html5/webvtt">WebVTT specification</a>
 */
public final class WebvttParser implements SubtitleParser {

  private static final String TAG = "WebvttParser";

  private static final Pattern CUE_SETTING = Pattern.compile("(\\S+?):(\\S+)");

  private final PositionHolder positionHolder;
  private final StringBuilder textBuilder;

  public WebvttParser() {
    positionHolder = new PositionHolder();
    textBuilder = new StringBuilder();
  }

  @Override
  public final boolean canParse(String mimeType) {
    return MimeTypes.TEXT_VTT.equals(mimeType);
  }

  @Override
  public final WebvttSubtitle parse(InputStream inputStream) throws IOException {
    BufferedReader webvttData = new BufferedReader(new InputStreamReader(inputStream, C.UTF8_NAME));

    // Validate the first line of the header, and skip the remainder.
    WebvttParserUtil.validateWebvttHeaderLine(webvttData);
    while (!TextUtils.isEmpty(webvttData.readLine())) {}

    // Process the cues and text.
    ArrayList<WebvttCue> subtitles = new ArrayList<WebvttCue>();
    Matcher cueHeaderMatcher;
    while ((cueHeaderMatcher = WebvttParserUtil.findNextCueHeader(webvttData)) != null) {
      long cueStartTime;
      long cueEndTime;
      try {
        // Parse the cue start and end times.
        cueStartTime = WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(1));
        cueEndTime = WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(2));
      } catch (NumberFormatException e) {
        System.out.println("Skipping cue with bad header: " + cueHeaderMatcher.group());
        continue;
      }

      // Default cue settings.
      float cueLine = Cue.DIMEN_UNSET;
      int cueLineType = Cue.TYPE_UNSET;
      int cueLineAnchor = Cue.TYPE_UNSET;
      float cuePosition = Cue.DIMEN_UNSET;
      int cuePositionAnchor = Cue.TYPE_UNSET;
      float cueWidth = Cue.DIMEN_UNSET;

      // Parse the cue settings list.
      Matcher cueSettingMatcher = CUE_SETTING.matcher(cueHeaderMatcher.group(3));
      while (cueSettingMatcher.find()) {
        String name = cueSettingMatcher.group(1);
        String value = cueSettingMatcher.group(2);
        try {
          if ("line".equals(name)) {
            parseLineAttribute(value, positionHolder);
            cueLine = positionHolder.position;
            cueLineType = positionHolder.lineType;
            cueLineAnchor = positionHolder.positionAnchor;
          } else if ("align".equals(name)) {
            System.out.println("Ignore alignment");
          } else if ("position".equals(name)) {
            parsePositionAttribute(value, positionHolder);
            cuePosition = positionHolder.position;
            cuePositionAnchor = positionHolder.positionAnchor;
          } else if ("size".equals(name)) {
            cueWidth = parsePercentage(value);
          } else {
            System.out.println("Unknown cue setting " + name + ":" + value);
          }
        } catch (NumberFormatException e) {
          System.out.println("Skipping bad cue setting: " + cueSettingMatcher.group());
        }
      }

      // Parse the cue text.
      textBuilder.setLength(0);
      String line;
      while ((line = webvttData.readLine()) != null && !line.isEmpty()) {
        if (textBuilder.length() > 0) {
          textBuilder.append("\n");
        }
        textBuilder.append(line.trim());
      }

      CharSequence cueText = textBuilder.toString();

      WebvttCue cue = new WebvttCue(cueStartTime, cueEndTime, cueText, cueLine,
          cueLineType, cueLineAnchor, cuePosition, cuePositionAnchor, cueWidth);
      subtitles.add(cue);
    }

    return new WebvttSubtitle(subtitles);
  }

  private static void parseLineAttribute(String s, PositionHolder out)
      throws NumberFormatException {
    int lineAnchor;
    int commaPosition = s.indexOf(",");
    if (commaPosition != -1) {
      lineAnchor = parsePositionAnchor(s.substring(commaPosition + 1));
      s = s.substring(0, commaPosition);
    } else {
      lineAnchor = Cue.TYPE_UNSET;
    }
    float line;
    int lineType;
    if (s.endsWith("%")) {
      line = parsePercentage(s);
      lineType = Cue.LINE_TYPE_FRACTION;
    } else {
      line = Integer.parseInt(s);
      lineType = Cue.LINE_TYPE_NUMBER;
    }
    out.position = line;
    out.positionAnchor = lineAnchor;
    out.lineType = lineType;
  }

  private static void parsePositionAttribute(String s, PositionHolder out)
      throws NumberFormatException {
    int positionAnchor;
    int commaPosition = s.indexOf(",");
    if (commaPosition != -1) {
      positionAnchor = parsePositionAnchor(s.substring(commaPosition + 1));
      s = s.substring(0, commaPosition);
    } else {
      positionAnchor = Cue.TYPE_UNSET;
    }
    out.position = parsePercentage(s);
    out.positionAnchor = positionAnchor;
    out.lineType = Cue.TYPE_UNSET;
  }

  private static float parsePercentage(String s) throws NumberFormatException {
    if (!s.endsWith("%")) {
      throw new NumberFormatException("Percentages must end with %");
    }
    s = s.substring(0, s.length() - 1);
    return Float.parseFloat(s) / 100;
  }

  private static int parsePositionAnchor(String s) {
	  if ("start".equals(s)) {
		  return Cue.ANCHOR_TYPE_START;
	  } else if ("middle".equals(s)) {
		  return Cue.ANCHOR_TYPE_MIDDLE;
	  } else if ("end".equals(s)) {
		  return Cue.ANCHOR_TYPE_END;
	  }
	  
	  System.out.println("Invalid anchor value: " + s);
	  return Cue.TYPE_UNSET;
  }

  private static final class PositionHolder {

    public float position;
    public int positionAnchor;
    public int lineType;

  }

}
