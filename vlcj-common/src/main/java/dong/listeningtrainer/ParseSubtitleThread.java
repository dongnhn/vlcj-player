package dong.listeningtrainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.android.exoplayer.ParserException;
import com.google.android.exoplayer.text.Subtitle;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.text.ssa.SsaParser;
import com.google.android.exoplayer.text.subrip.SubripParser;
import com.google.android.exoplayer.text.ttml.TtmlParser;
import com.google.android.exoplayer.text.tx3g.Tx3gParser;
import com.google.android.exoplayer.text.webvtt.WebvttParser;
import com.google.android.exoplayer.util.Util;
import com.google.common.io.Files;

public class ParseSubtitleThread extends Thread {
	private static final Map<String, SubtitleParser> supportedParsers = new LinkedHashMap<String, SubtitleParser>();
	
	static {
		supportedParsers.put("srt", new SubripParser());

		SsaParser ssaParser = new SsaParser();
		supportedParsers.put("ssa", ssaParser);
		supportedParsers.put("ass", ssaParser);
		
		supportedParsers.put("vtt", new WebvttParser());
		
		TtmlParser ttmlParser = new TtmlParser();
		supportedParsers.put("ttml", ttmlParser);
		supportedParsers.put("dfxp", ttmlParser);
		supportedParsers.put("xml", ttmlParser);
		
		supportedParsers.put("tx3g", new Tx3gParser());
	}
	
	private File file;
	private SubtitleParseListener listener;

	public interface SubtitleParseListener {
		void onParseComplete(Subtitle subtitle, Exception e);
	}
	
	public ParseSubtitleThread(File file, SubtitleParseListener listener) {
		super();
		this.file = file;
		this.listener = listener;
	}

	@Override
	public void run() {
		List<SubtitleParser> parsers = getParsers(file);
		Subtitle subtitle = null;
		for (SubtitleParser parser : parsers) {
			try {
				subtitle = parser.parse(new FileInputStream(file));
			} catch (IOException ignored) {}
		}
		
		if (subtitle == null || subtitle.getEventTimeCount() == 0) {
			notifyListener(listener, null, new ParserException());
		} else {
			notifyListener(listener, subtitle, null);
		}
	}
	
	void notifyListener(SubtitleParseListener listener, Subtitle subtitle, Exception e) {
		if (listener != null) {
			listener.onParseComplete(subtitle, e);
		}
	}
	
	static List<SubtitleParser> getParsers(File file) {
		if (file != null) {
			String ext = Util.toLowerInvariant(Files.getFileExtension(file.getName()));
			if (supportedParsers.containsKey(ext)) {
				return Collections.singletonList(supportedParsers.get(ext));
			}
		}
		
		return Collections.emptyList();
	}
}
