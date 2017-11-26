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
import static uk.co.caprica.vlcjplayer.time.Time.formatTime;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.google.common.eventbus.Subscribe;

import net.miginfocom.swt.MigLayout;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.event.TickEvent;
import uk.co.caprica.vlcjplayer.swt.view.BaseComposite;

final class Seekbar extends BaseComposite {

    private final Label timeLabel;

    private final Scale positionSlider;

    private final Label durationLabel;

    private long time;

    private final MediaPlayer mediaPlayer;

    private final AtomicBoolean sliderChanging = new AtomicBoolean();

    private final AtomicBoolean positionChanging = new AtomicBoolean();

    public Seekbar(Composite parent) {
    	super(parent, SWT.NO_FOCUS);
    	mediaPlayer = application().mediaPlayerComponent().getMediaPlayer();
    	setLayout(new MigLayout("fill, insets 0 0 0 0", "[][grow][]", "[]"));
    	
        timeLabel = new Label(this, SWT.NONE);
        timeLabel.setText("-:--:--");
        timeLabel.setLayoutData("w pref+10px::");
        
        positionSlider = new Scale(this, SWT.HORIZONTAL);
        positionSlider.setLayoutData("grow");
        positionSlider.setMinimum(0);
        positionSlider.setMaximum(1000);
        positionSlider.setSelection(0);
        
        durationLabel = new Label(this, SWT.NONE);
        durationLabel.setText("-:--:--");
        durationLabel.setLayoutData("w pref+10px::");
        
        positionSlider.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseDown(MouseEvent e) {
        		if (!positionChanging.get()) {
        			sliderChanging.set(true);
        		}
        	}
        	
        	@Override
        	public void mouseUp(MouseEvent e) {
        		sliderChanging.set(false);
        		mediaPlayer.setPosition(positionSlider.getSelection() / 1000.0f);
        	}
		});
        
        application().subscribe(this);
    }

    private void refresh() {
        timeLabel.setText(formatTime(time));

        if (!sliderChanging.get()) {
            int value = (int) (mediaPlayer.getPosition() * 1000.0f);
            positionChanging.set(true);
            positionSlider.setSelection(value);
            positionChanging.set(false);
        }
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
    	setTime(newTime);
    }
    
    @Override
    public void stopped(MediaPlayer mediaPlayer) {
    	setTime(0);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
    	setTime(0);
    }
    
    private void setTime(long time) {
		this.time = time;
	}
    
    @Override
    public void mediaDurationChanged(MediaPlayer mediaPlayer, final long newDuration) {
    	getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				durationLabel.setText(formatTime(newDuration));
			}
		});
    }

    @Subscribe
    public void onTick(TickEvent tick) {
    	getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
    }
}
