import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class PlayerManager {
	private AdvancedPlayer player;
	private AudioDevice device = new JavaSoundAudioDevice();
	private Song playing = null;
	private BottomMenu bottomPanel = null;
	
	public AdvancedPlayer getPlayer(){
		return player;
	}
	public void setBottomPanel(BottomMenu bottomPanel){
		this.bottomPanel = bottomPanel;
	}
	public boolean isPlaying(Song song){
		return playing == song;
	}
	public void play(Song song) {
		try {
			if(bottomPanel != null){
				bottomPanel.setSong(song);
			}
			if(player != null){
				player.stop();
				player = null;
				if(playing == song){//chceme skončiť ak sa ide prahrať niečo žnovu
					playing = null;
					return;
				}
			}
			long start = System.currentTimeMillis();
			playing = song;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						player = new AdvancedPlayer(new FileInputStream(new File(song.getAbsolutePath())));
						player.setPlayBackListener(new PlaybackListener() {
						    @Override
						    public void playbackFinished(PlaybackEvent event) {
						        System.out.println("frame: " + event.getFrame());
//						        System.out.println(System.currentTimeMillis() - start);
//						        System.out.println("pos: " + device.getPosition());
//						        device.flush();
//						        device.close();
						    }
						});
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
