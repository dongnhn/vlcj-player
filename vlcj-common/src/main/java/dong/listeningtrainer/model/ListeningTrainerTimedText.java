package dong.listeningtrainer.model;

import java.util.ArrayList;
import java.util.List;

import com.google.android.exoplayer.TextUtils;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.Subtitle;

public class ListeningTrainerTimedText {
	public static final int TIME_MARGIN = 250; // milliseconds
	
	private static final int RETRY_COUNT = 9;
	private static final int POINT_FOR_CHAR = RETRY_COUNT + 1;
	
	private String text;
	private long startTimeMs;
	private long endTimeMs;

	private StringBuilder displayTextSb;
	private int hiddenIndex = 0;
	private int nRetry = 0;
	
	private int totalPoints = 0;
	private int currentPoints = 0;

	public ListeningTrainerTimedText(String text, long startTimeMs, long endTimeMs) {
		this.text = text;
		this.startTimeMs = startTimeMs;
		this.endTimeMs = endTimeMs;
		this.totalPoints = calculateTotalPoints(text);
		this.hiddenIndex = nextHiddenCharacterIndex(text, 0);
		
		if (text != null) {
			displayTextSb = new StringBuilder(text.length());
			
			for (int i = 0, len = text.length(); i < len; i++) {
				if (isHiddenCharacter(text, i)) {
					displayTextSb.append('*');
				} else {
					displayTextSb.append(text.charAt(i));
				}
			}
		} else {
			displayTextSb = new StringBuilder(0);
		}
	}

	private static boolean isHiddenCharacter(String text, int index) {
		if (text != null && index < text.length()) {
			return Character.isLetterOrDigit(text.codePointAt(index));
		}
		return false;
	}
	
	private static int nextHiddenCharacterIndex(String text, int from) {
		if (text != null) {
			while (from < text.length() && !isHiddenCharacter(text, from)) {
				from++;
			}
			return from;
		}
		
		return -1;
	}

	private static int calculateTotalPoints(String text) {
		int totalPoints = 0;
		if (text != null) {
			for (int i = 0, len = text.length(); i < len; i++) {
				if (isHiddenCharacter(text, i)) {
					totalPoints += POINT_FOR_CHAR;
				}
			}
		}
		
		return totalPoints;
	}

	public long getStartTime() {
		return startTimeMs;
	}

	public long getEndTime() {
		return endTimeMs;
	}
	
	public String getDisplayText() {
		return displayTextSb.toString();
	}
	
	public String getOriginalText() {
		return text;
	}
	
	public boolean isCompleted() {
		return (hiddenIndex >= displayTextSb.length());
	}
	
	public float getScorePercent() {
		if (totalPoints > 0) {
			return currentPoints * 1.0f / totalPoints;
		} else {
			return -1f;
		}
	}
	
	public boolean inputChar(char c) {
		if (hiddenIndex >= displayTextSb.length()) {
			return true;
		}
		
		if (text != null && text.length() > 0) {
			char correctChar = text.charAt(hiddenIndex);
			if (Character.toLowerCase(correctChar) == Character.toLowerCase(c)) {
				revealCurrentCharacter();
				return true;
			} else {
				if (nRetry >= RETRY_COUNT) {
					revealCurrentCharacter();
					return true;
				} else {
					nRetry++;
					return false;
				}
			}
		}
		
		return false;
	}

	private void revealCurrentCharacter() {
		// update display text
		displayTextSb.setCharAt(hiddenIndex, text.charAt(hiddenIndex));
		// move to next character
		hiddenIndex = nextHiddenCharacterIndex(text, hiddenIndex + 1);
		// update points
		currentPoints += (POINT_FOR_CHAR - nRetry);
		nRetry = 0;
	}

	public static List<ListeningTrainerTimedText> convert(Subtitle subtitle, long maxDuration) {
		ArrayList<ListeningTrainerTimedText> result = new ArrayList<ListeningTrainerTimedText>();
		
		if (subtitle != null && subtitle.getEventTimeCount() > 1) {
			StringBuilder cuesContentBuilder = new StringBuilder();
			for (int i = 0, j = 1, len = subtitle.getEventTimeCount(); j < len; i++, j++) {
				long beginTime = subtitle.getEventTime(i);
				long endTime = subtitle.getEventTime(j);
				if (endTime != beginTime) {
					long searchTime = (beginTime + endTime) / 2;
					buildCuesContent(subtitle.getCues(searchTime), cuesContentBuilder);
					String cuesContent = cuesContentBuilder.toString().trim();
					if (!TextUtils.isEmpty(cuesContent)) {
						long startTimeMs = Math.max(0, beginTime / 1000 - TIME_MARGIN);
						long endTimeMs = Math.min((endTime / 1000 + TIME_MARGIN), maxDuration);
						ListeningTrainerTimedText tt = new ListeningTrainerTimedText(cuesContent, startTimeMs, endTimeMs);
						result.add(tt);
					}
				}
			}
		}
		
		return result;
	}

	private static void buildCuesContent(List<Cue> cues, StringBuilder cuesContentBuilder) {
		cuesContentBuilder.setLength(0);
		if (cues != null && !cues.isEmpty()) {
			boolean firstTime = true;
			for (Cue cue : cues) {
				if (firstTime) {
					firstTime = true;
				} else {
					cuesContentBuilder.append("\n");
				}
				
				cuesContentBuilder.append(cleanupHtmlTag(cue.text.toString()));
			}
		}
	}

	private static String cleanupHtmlTag(String content) {
		if (content != null) {
			return content.trim().replaceAll("<br.*?>", "\n").replaceAll("<.*?>", "");
		}
		
		return content;
	}
}
