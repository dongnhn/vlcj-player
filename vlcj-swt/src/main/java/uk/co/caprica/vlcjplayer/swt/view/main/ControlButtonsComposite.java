/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */

package uk.co.caprica.vlcjplayer.swt.view.main;

import static uk.co.caprica.vlcjplayer.swt.SwtApplication.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

import net.miginfocom.swt.MigLayout;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.swt.view.BaseComposite;

final class ControlButtonsComposite extends BaseComposite {

    private final Image playIcon = newIcon("play");

    private final Image pauseIcon = newIcon("pause");
    
    private final Image volumeHighIcon = newIcon("volume-high");

    private final Image volumeMutedIcon = newIcon("volume-muted");

    private final Label playPauseButton;

    private final Label muteButton;

    private final Scale volumeSlider;

    public ControlButtonsComposite(Composite parent) {
    	super(parent, SWT.NONE);
    	
    	setLayout(new MigLayout("fill, insets 0 0 0 0", "[]12[]0[]0[]12[]0[]12[]push[]0[]", "[]"));
    	
    	final MediaPlayer mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
    	
        playPauseButton = new BigButton(this, "play") {
        	protected void onButtonClicked() {
				if (!mediaPlayer.isPlaying()) {
					mediaPlayer.play();
				} else {
					mediaPlayer.pause();
				}
				updatePlayPauseIcon(mediaPlayer);
        	};
        }.button();
        
        new StandardButton(this, "previous");
        new StandardButton(this, "stop") {
        	@Override
        	protected void onButtonClicked() {
        		mediaPlayer.stop();
        	}
        };
        new StandardButton(this, "next");
        new StandardButton(this, "fullscreen") {
        	@Override
        	protected void onButtonClicked() {
        		application().mediaPlayerComponent().toggleFullScreen();
        	}
        };
        new StandardButton(this, "extended") {
        	@Override
        	protected void onButtonClicked() {
        		// TODO Auto-generated method stub
        	}
        };
        new StandardButton(this, "snapshot") {
        	@Override
        	protected void onButtonClicked() {
        		// TODO Auto-generated method stub
    			//        snapshotButton.setAction(mediaPlayerActions.videoSnapshotAction());
        	}
        };
        
        muteButton = new StandardButton(this, "volume-high") {
        	protected void onButtonClicked() {
        		boolean result = mediaPlayer.mute();
        		updateMuteIcon(result);
        	};
        }.button();
        
        volumeSlider = new Scale(this, SWT.HORIZONTAL);
        volumeSlider.setLayoutData("wmax 100");
        volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
        volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
        volumeSlider.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mediaPlayer.setVolume(volumeSlider.getSelection());
			}
		});
    }
    
    @Override
    public void playing(MediaPlayer mediaPlayer) {
    	updatePlayPauseIcon(mediaPlayer);
    	updateVolumeValue(mediaPlayer.getVolume());
    }
    
	@Override
    public void stopped(MediaPlayer mediaPlayer) {
    	updatePlayPauseIcon(mediaPlayer);
    }
    
    @Override
    public void finished(MediaPlayer mediaPlayer) {
    	updatePlayPauseIcon(mediaPlayer);
    }
    
    @Override
    public void error(MediaPlayer mediaPlayer) {
    	updatePlayPauseIcon(mediaPlayer);
    }
    
    private void updatePlayPauseIcon(final MediaPlayer mediaPlayer) {
    	getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				playPauseButton.setImage(mediaPlayer.isPlaying() ? pauseIcon : playIcon);
			}
		});
    }
    
    private void updateMuteIcon(final boolean muted) {
    	getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (muted) {
					muteButton.setImage(volumeMutedIcon);
				} else {
					muteButton.setImage(volumeHighIcon);
				}
			}
		});
    }
    
    private void updateVolumeValue(final int volume) {
    	getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				volumeSlider.setSelection(volume);
			}
		});
	}
    
    private class BigButton extends StandardButton {
		public BigButton(Composite parent, String iconName) {
			super(parent, iconName);
			button().setLayoutData("sg 1, w pref+20px, h pref+20px");
		}
    }
    
    private class StandardButton implements MouseListener {
    	private Label label;

		public StandardButton(Composite parent, String iconName) {
    		label = new Label(parent, SWT.NONE);
    		label.setLayoutData("sg 1, w pref+16px, h pref+16px");
    		label.setImage(newIcon(iconName));
    		label.addMouseListener(this);
		}
    	
    	public Label button() {
    		return label;
    	}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
		}

		@Override
		public void mouseUp(MouseEvent e) {
			onButtonClicked();
		}

		protected void onButtonClicked() {
		}
    }

    private Image newIcon(String name) {
        return new Image(getDisplay(), getClass().getResourceAsStream("/icons/buttons/" + name + ".png"));
    }
}
