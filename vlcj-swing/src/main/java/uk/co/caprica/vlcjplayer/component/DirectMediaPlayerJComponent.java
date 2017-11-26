package uk.co.caprica.vlcjplayer.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.swing.JPanel;

import com.sun.jna.Memory;

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class DirectMediaPlayerJComponent extends Panel implements AbstractMediaPlayerComponent<Component> {

	private DirectMediaPlayerComponent mediaComponent;
	private AutoResizeVideoSurfacePanel videoSurfacePanel;
	
	private BufferedImage videoFrame;
	private double sourceRatio = 0;
	
	public DirectMediaPlayerJComponent() {
		setBackground(Color.black);
        setLayout(new BorderLayout());
        videoSurfacePanel = new AutoResizeVideoSurfacePanel();
        add(videoSurfacePanel, BorderLayout.CENTER);
        
        BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
			@Override
			public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
				sourceRatio = (double) sourceHeight / (double) sourceWidth;

				GraphicsDevice defaultScreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				Rectangle screenBounds = defaultScreenDevice.getDefaultConfiguration().getBounds();
				Rectangle2D bufferBounds = new Rectangle2D.Double();
				
				fitCenter(sourceRatio, screenBounds, bufferBounds);
				
				int bufferWidth = (int) bufferBounds.getWidth();
				int bufferHeight = (int) bufferBounds.getHeight();
				
				videoFrame = defaultScreenDevice.getDefaultConfiguration().createCompatibleImage(bufferWidth, bufferHeight);
				
				return new RV32BufferFormat(bufferWidth, bufferHeight);
			}
		};
		
		mediaComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
			@Override
			protected RenderCallback onGetRenderCallback() {
				return new DirectRenderCallback();
			}
		};
	}
	
	@Override
	public MediaPlayer getMediaPlayer() {
		return mediaComponent.getMediaPlayer();
	}

	@Override
	public void release() {
		mediaComponent.release();
	}

	@Override
	public MediaPlayerFactory getMediaPlayerFactory() {
		return mediaComponent.getMediaPlayerFactory();
	}

	@Override
	public void toggleFullScreen() {
	}

	@Override
	public Component getContentComponent() {
		return this;
	}

	@Override
	public Component getVideoSurface() {
		return videoSurfacePanel;
	}

	@Override
	public void setCursorEnabled(boolean enabled) {
	}
	
	private class AutoResizeVideoSurfacePanel extends JPanel {
		private Rectangle2D targetRect;
		private Rectangle2D resultRect;

		public AutoResizeVideoSurfacePanel() {
			setBackground(Color.black);
			setOpaque(true);
			targetRect = new Rectangle2D.Double();
			resultRect = new Rectangle2D.Double();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if (sourceRatio == 0) {
				return;
			}

			Graphics2D g2 = (Graphics2D) g;
			targetRect.setRect(0, 0, getWidth(), getHeight());
			fitCenter(sourceRatio, targetRect, resultRect);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.drawImage(videoFrame,(int) resultRect.getX(), (int) resultRect.getY(), (int) resultRect.getMaxX(), (int) resultRect.getMaxY(),
					0, 0, videoFrame.getWidth(), videoFrame.getHeight(), null);
		}
	}
	
	private class DirectRenderCallback implements RenderCallback {
		@Override
		public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
			ByteBuffer byteBuffer = nativeBuffers[0].getByteBuffer(0, nativeBuffers[0].size());
			setRGB(videoFrame, 0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), byteBuffer.asIntBuffer(), 0, bufferFormat.getWidth());

			videoSurfacePanel.repaint();
		}
	}
	
	static void setRGB(BufferedImage videoFrame, int startX, int startY, int w, int h, IntBuffer intBuffer, int offset, int scansize) {
		int yoff = offset;
		int off;
		Object pixel = null;
		
		for (int y = startY; y < startY + h; y++, yoff += scansize) {
			off = yoff;
			for (int x = startX; x < startX + w; x++) {
				pixel = videoFrame.getColorModel().getDataElements(intBuffer.get(off++), pixel);
				videoFrame.getRaster().setDataElements(x, y, pixel);
			}
		}
	}
	
	static void fitCenter(Rectangle2D sourceRect, Rectangle2D targetRect, Rectangle2D resultRect) {
		double ratio = (sourceRect.getHeight() / sourceRect.getWidth());
		fitCenter(ratio, targetRect, resultRect);
	}
	
	static void fitCenter(double ratio, Rectangle2D targetRect, Rectangle2D resultRect) {
		double fitWidth = targetRect.getWidth();
		double fitHeight = Math.ceil(fitWidth * ratio);
		
		if (fitHeight > targetRect.getHeight()) {
			fitHeight = targetRect.getHeight();
			fitWidth = Math.ceil(fitHeight / ratio);
		}
		
		double fitX = Math.ceil((targetRect.getWidth() - fitWidth) / 2);
		double fitY = Math.ceil((targetRect.getHeight() - fitHeight) / 2);
		
		resultRect.setRect(fitX, fitY, fitWidth, fitHeight);
	}
}
