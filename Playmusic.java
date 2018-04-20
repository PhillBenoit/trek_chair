/*
 * 
 * Class for playing WAV files
 * 
 */

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Playmusic implements Runnable {
	
	//main method only used for sound testing
	public static void main(String[] args){
		Thread t = new Thread(new Playmusic("redalert.wav", 1));
		t.start();
	}

	//path of the file to play
	private String path;

	//denotes weather the file will loop or not
	private int loops;

	//constructor
	public Playmusic(String s, int i) {
		
		//sets file based on path of installed sounds
		path = "/opt/sounds/" + s;
		
		//loops continuously if passed loop value is not 0
		loops = (i == 0) ? 0 : Clip.LOOP_CONTINUOUSLY;
	}

	//runs the thread
	@Override
	public void run() {
		
		//file and stream information
		AudioInputStream audioIn = null;
		Clip clip = null;
		try {

			//assign file, stream and looping information
			audioIn = AudioSystem.getAudioInputStream(new File(path));
			clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.loop(loops);

			//start playback
			clip.start();

			//keeps the thread alive while the sound is playing
			do Thread.sleep(clip.getMicrosecondLength()/1000);

			//keeps thread alive for consecutive loops
			while (loops < 0);

			//close file and stream
			clip.close();
			audioIn.close();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException
				| InterruptedException | IllegalArgumentException  e1) {
		}

		//close any file / stream if open after error handling
		try {
			if (clip != null && clip.isOpen()) clip.close();
			if (!audioIn.equals(null)) audioIn.close();
		} catch (IOException e) {
		}
	}
}
