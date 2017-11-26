/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.google.android.exoplayer.text.ssa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.TextUtils;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.Subtitle;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.util.LongArray;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.ParsableByteArray;
import com.google.android.exoplayer.util.Util;

/**
 * A {@link SimpleSubtitleDecoder} for SSA/ASS.
 */
public final class SsaParser implements SubtitleParser {

	private static final String TAG = "SsaDecoder";

	private static final Pattern SSA_TIMECODE_PATTERN = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)(?::|\\.)(\\d+)");
	private static final String FORMAT_LINE_PREFIX = "Format: ";
	private static final String DIALOGUE_LINE_PREFIX = "Dialogue: ";

	private final boolean haveInitializationData;

	private int formatKeyCount;
	private int formatStartIndex;
	private int formatEndIndex;
	private int formatTextIndex;

	public SsaParser() {
		this(null);
	}

	/**
	 * @param initializationData
	 *            Optional initialization data for the decoder. If not null, the
	 *            initialization data must consist of two byte arrays. The first
	 *            must contain an SSA format line. The second must contain an
	 *            SSA header that will be assumed common to all samples.
	 */
	public SsaParser(List<byte[]> initializationData) {
		if (initializationData != null) {
			haveInitializationData = true;
			String formatLine = new String(initializationData.get(0));
			assert (formatLine.startsWith(FORMAT_LINE_PREFIX));
			parseFormatLine(formatLine);
			try {
				parseHeader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(initializationData.get(1)))));
			} catch (IOException ignored) {}
		} else {
			haveInitializationData = false;
		}
	}

	/**
	 * Parses the header of the subtitle.
	 *
	 * @param reader
	 *            A {@link ParsableByteArray} from which the header should be
	 *            read.
	 * @throws IOException 
	 */
	private void parseHeader(BufferedReader reader) throws IOException {
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			// TODO: Parse useful data from the header.
			if (currentLine.startsWith("[Events]")) {
				// We've reached the event body.
				return;
			}
		}
	}

	/**
	 * Parses the event body of the subtitle.
	 *
	 * @param reader
	 *            A {@link ParsableByteArray} from which the body should be
	 *            read.
	 * @param cues
	 *            A list to which parsed cues will be added.
	 * @param cueTimesUs
	 *            An array to which parsed cue timestamps will be added.
	 * @throws IOException 
	 */
	private void parseEventBody(BufferedReader reader, List<Cue> cues, LongArray cueTimesUs) throws IOException {
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			if (!haveInitializationData && currentLine.startsWith(FORMAT_LINE_PREFIX)) {
				parseFormatLine(currentLine);
			} else if (currentLine.startsWith(DIALOGUE_LINE_PREFIX)) {
				parseDialogueLine(currentLine, cues, cueTimesUs);
			}
		}
	}

	/**
	 * Parses a format line.
	 *
	 * @param formatLine
	 *            The line to parse.
	 */
	private void parseFormatLine(String formatLine) {
		String[] values = TextUtils.split(formatLine.substring(FORMAT_LINE_PREFIX.length()), ",");
		formatKeyCount = values.length;
		formatStartIndex = C.INDEX_UNSET;
		formatEndIndex = C.INDEX_UNSET;
		formatTextIndex = C.INDEX_UNSET;
		for (int i = 0; i < formatKeyCount; i++) {
			String key = Util.toLowerInvariant(values[i].trim());
			if ("start".equals(key)) {
				formatStartIndex = i;
			} else if ("end".equals(key)) {
				formatEndIndex = i;
			} else if ("text".equals(key)) {
				formatTextIndex = i;
			}
		}
	}

	/**
	 * Parses a dialogue line.
	 *
	 * @param dialogueLine
	 *            The line to parse.
	 * @param cues
	 *            A list to which parsed cues will be added.
	 * @param cueTimesUs
	 *            An array to which parsed cue timestamps will be added.
	 */
	private void parseDialogueLine(String dialogueLine, List<Cue> cues, LongArray cueTimesUs) {
		if (formatKeyCount == 0) {
			System.out.println("Skipping dialogue line before format: " + dialogueLine);
			return;
		}

		String[] lineValues = dialogueLine.substring(DIALOGUE_LINE_PREFIX.length()).split(",", formatKeyCount);
		long startTimeUs = SsaParser.parseTimecodeUs(lineValues[formatStartIndex]);
		if (startTimeUs == C.TIME_UNSET) {
			System.out.println("Skipping invalid timing: " + dialogueLine);
			return;
		}

		long endTimeUs = C.TIME_UNSET;
		String endTimeString = lineValues[formatEndIndex];
		if (!endTimeString.trim().isEmpty()) {
			endTimeUs = SsaParser.parseTimecodeUs(endTimeString);
			if (endTimeUs == C.TIME_UNSET) {
				System.out.println("Skipping invalid timing: " + dialogueLine);
				return;
			}
		}

		String text = lineValues[formatTextIndex].replaceAll("\\{.*?\\}", "").replaceAll("\\\\N", "\n")
				.replaceAll("\\\\n", "\n");
		cues.add(new Cue(text));
		cueTimesUs.add(startTimeUs);
		if (endTimeUs != C.TIME_UNSET) {
			cues.add(null);
			cueTimesUs.add(endTimeUs);
		}
	}

	/**
	 * Parses an SSA timecode string.
	 *
	 * @param timeString
	 *            The string to parse.
	 * @return The parsed timestamp in microseconds.
	 */
	public static long parseTimecodeUs(String timeString) {
		Matcher matcher = SSA_TIMECODE_PATTERN.matcher(timeString);
		if (!matcher.matches()) {
			return C.TIME_UNSET;
		}
		long timestampUs = Long.parseLong(matcher.group(1)) * 60 * 60 * C.MICROS_PER_SECOND;
		timestampUs += Long.parseLong(matcher.group(2)) * 60 * C.MICROS_PER_SECOND;
		timestampUs += Long.parseLong(matcher.group(3)) * C.MICROS_PER_SECOND;
		timestampUs += Long.parseLong(matcher.group(4)) * 10000; // 100ths of a
																	// second.
		return timestampUs;
	}

	@Override
	public boolean canParse(String mimeType) {
		return MimeTypes.TEXT_SSA.equals(mimeType);
	}

	@Override
	public Subtitle parse(InputStream inputStream) throws IOException {
		ArrayList<Cue> cues = new ArrayList<Cue>();
		LongArray cueTimesUs = new LongArray();

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, C.UTF8_NAME));
		if (!haveInitializationData) {
			parseHeader(reader);
		}
		parseEventBody(reader, cues, cueTimesUs);

		Cue[] cuesArray = new Cue[cues.size()];
		cues.toArray(cuesArray);
		long[] cueTimesUsArray = cueTimesUs.toArray();
		return new SsaSubtitle(cuesArray, cueTimesUsArray);
	}

}
