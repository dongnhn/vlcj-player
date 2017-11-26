package dong.listeningtrainer.model;

import java.util.List;

import com.google.android.exoplayer.text.Subtitle;

public class ListeningTrainer {
	public interface Listener {
		void onTextChanged(ListeningTrainerTimedText newText, String description, boolean hasNext, boolean hasPrevious);
		
		void onDisplayTextChanged(ListeningTrainerTimedText text);
		
		void onWrongCharacterEntered();
	}
	
	private List<ListeningTrainerTimedText> listTimedText;
	private int currentIndex;
	private Listener listener;

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public void startTraining(Subtitle subtitle, long start, long end) {
		if (subtitle != null) {
			this.listTimedText = ListeningTrainerTimedText.convert(subtitle, end);
			int currentIndex = 0;
			for (int i = 0, len = listTimedText.size(); i < len; i++) {
				if (listTimedText.get(i).getEndTime() >= start) {
					currentIndex = i;
					break;
				}
			}
			selectText(currentIndex);
		}
	}
	
	private void selectText(int index) {
		if (listTimedText == null || index < 0 || index >= listTimedText.size()) {
			return;
		}

		this.currentIndex = index;
		
		if (listener != null) {
			int size = listTimedText.size();
			String description = String.format("%s/%s", (index + 1), size);
			listener.onTextChanged(getCurrentText(), description, (index < size - 1), (index > 0));
		}
	}

	public ListeningTrainerTimedText getCurrentText() {
		if (listTimedText != null && currentIndex >= 0 && currentIndex < listTimedText.size()) {
			return listTimedText.get(currentIndex);
		}

		return null;
	}
	
	public void next() {
		selectText(currentIndex + 1);
	}
	
	public void previous() {
		selectText(currentIndex - 1);
	}
	
	public void enter(char c) {
		ListeningTrainerTimedText currentText = getCurrentText();
		if (currentText != null && !currentText.isCompleted() && Character.isLetterOrDigit(c)) {
			if (currentText.inputChar(c)) {
				if (listener != null) {
					listener.onDisplayTextChanged(currentText);
				}
			} else {
				if (listener != null) {
					listener.onWrongCharacterEntered();
				}
			}
		}
	}
}
