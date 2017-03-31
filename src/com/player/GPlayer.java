package com.player;


import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class GPlayer extends AdvancedPlayer{
	public GPlayer(InputStream arg0) throws JavaLayerException {
		super(arg0);
		
	}
	
	public boolean skip() throws JavaLayerException{
		return skipFrame();
	}

}
