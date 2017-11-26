package dong.listeningtrainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.miginfocom.swt.MigLayout;

public class RatingComposite extends Composite {
	
	private Label[] starLabels;
	
	public RatingComposite(Shell parent) {
		super(parent, SWT.NO_BACKGROUND);
		setLayout(new MigLayout("insets 0", "[]0[]0[]"));
		
		starLabels = new Label[3];
		Image icon = new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/ic_star.png"));
		for (int i = 0, len = starLabels.length; i< len; i++) {
			Label label = new Label(this, SWT.NONE);
			label.setImage(icon);
			label.setLayoutData("shrink");
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
		for (Label label : starLabels) {
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
