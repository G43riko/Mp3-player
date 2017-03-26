package com.player;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.gui.BottomMenu;
import com.library.Song;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

public class PlayerManager {
	private GPlayer player;
	private MpegAudioFileReader reader = new MpegAudioFileReader();
	private Player playerOld;
	private int pausedFrame;
	private int numberOfFrames;
//	private AudioDevice device = new JavaSoundAudioDevice();
	private Song playing = null;
	private BottomMenu bottomPanel = null;
	
//	public AdvancedPlayer getPlayer(){
//		return player;
//	}
	private void update(){
		bottomPanel.updateData();;
	}
	
	public void setBottomPanel(BottomMenu bottomPanel){
		this.bottomPanel = bottomPanel;
	}
	public boolean isPlaying(){
		return player != null;
	}
	public boolean isPlaying(Song song){
		return playing == song;
	}
	public Song getPlayingSong(){
		return playing;
	}
	public void stop(){
		player.stop();
		player = null;
		update();
	}
	public void play(Song song) {
		try {
			if(bottomPanel != null && song != null){
				bottomPanel.setSong(song);
			}
			if(player != null){
				player.stop();
				player = null;
				if(playing == song){//chceme skončiť ak sa ide prahrať niečo žnovu
//					playing = null;
					update();
					return;
				}
			}
//			long start = System.currentTimeMillis();
			if(song != null){
				playing = song;
			}
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						player = new GPlayer(new FileInputStream(new File(playing.getAbsolutePath())));
						
						player.setPlayBackListener(new PlaybackListener() {
						    @Override
						    public void playbackFinished(PlaybackEvent event) {
						    	pausedFrame = event.getFrame();
						    	System.out.println("pausedFrame: " + pausedFrame);
						    }
						});
						update();
						player.play();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
				}
			});
			
			t.start();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	public void playSkipTo(float i) {
		
	}
}
