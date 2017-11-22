package dong.listeningtrainer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class RatingView extends JPanel {
	
	private JLabel[] starLabels;
	
	public RatingView() {
		setOpaque(false);
		setLayout(new MigLayout("insets 0", "[]0[]0[]"));
		
		starLabels = new JLabel[3];
		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/ic_star.png"));
		for (int i = 0, len = starLabels.length; i< len; i++) {
			JLabel label = new JLabel();
			label.setIcon(icon);
			add(label, "shrink");
			label.setVisible(false);
			starLabels[i] = label;
		}
	}
	
	public void setRating(float rate) {
		if (rate >= 0 && rate <= 1.0f) {
			if (rate >= 0.96) {
				setVisibleStars(3);
			} else if (rate >= 0.5) {
				setVisibleStars(2);
			} else {
				setVisibleStars(1);
			}
		} else {
			resetAllStars();
		}
	}
	
	private void resetAllStars() {
		for (JLabel label : starLabels) {
			label.setVisible(false);
		}
	}

	private void setVisibleStars(int n) {
		resetAllStars();
		for (int i = starLabels.length - n, len = starLabels.length; i < len; i++) {
			starLabels[i].setVisible(true);
		}
	}
}
