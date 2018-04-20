/*
 * **********************************************************************
 * This file is modified from part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2016 Pi4J & 2017 - 2018 Star Trek Tucson Fan Club
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */


import java.io.IOException;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class KirkChair {

	//threads for sounds
	static private Thread alert_thread, button_thread;
	
	//path for sound files
	static private String path;

	//array index for the light with active sound on the right side
	static private int active_alert_index;

	//helps prevent processing multiple inputs simultaneously
	static private boolean processing_request;

	//amount of time (in milliseconds) between valid use inputs
	static final private long BUTTON_DELAY = 500;

	//minimum time index for next input
	static private long next_input;

	public static void main(String args[]) throws InterruptedException,
	UnsupportedBusNumberException, IOException {

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		// create MCP23017 GPIO provider left switches
		//----------------------------------------------------------------------
		final MCP23017GpioProvider left_switches = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);

		// provision gpio input pins from MCP23017
		GpioPinDigitalInput switch_speed[] = {
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A0, "dark_grey-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A1, "grey-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A2, "powder_blue-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A3, "sky_blue-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A4, "blue-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A5, "white-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A6, "yellow-speed", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_A7, "red-speed", PinPullResistance.PULL_UP),
		};

		GpioPinDigitalInput switch_amount[] = {
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B7, "dark_grey-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B6, "grey-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B5, "powder_blue-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B4, "sky_blue-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B3, "blue-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B2, "white-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B1, "yellow-amount", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B0, "red-amount", PinPullResistance.PULL_UP)
		};

		// create and register gpio pin listeners
		gpio.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

				//tests for time between presses, simultaneous input and
				//proper switch position before processing the request.
				if (System.currentTimeMillis() > next_input && !processing_request &&
						event.getState() == PinState.LOW) {
					processing_request = true;
					button_sound();
					processing_request = false;
				}
			}
		}, switch_speed);

		gpio.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

				//tests for time between presses, simultaneous input and
				//proper switch position before processing the request.
				if (System.currentTimeMillis() > next_input && !processing_request &&
						event.getState() == PinState.LOW) {
					processing_request = true;
					button_sound();
					processing_request = false;
				}
			}
		}, switch_amount);


		//left lights
		//----------------------------------------------------------------------
		final MCP23017GpioProvider left_leds = new MCP23017GpioProvider(I2CBus.BUS_1, 0x23);

		// provision gpio output pins and make sure they are all LOW at startup
		GpioPinDigitalOutput led_left[] = {
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_B0, "rectangle-1", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_B1, "rectangle-2", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_B2, "rectangle-3", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_B3, "rectangle-4", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_B4, "rectangle-5", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_B5, "rectangle-6", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_A7, "circle-3", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_A6, "circle-2", PinState.LOW),
				gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_A5, "circle-1", PinState.LOW)
		};

		//right panel
		//----------------------------------------------------------------------
		final MCP23017GpioProvider right = new MCP23017GpioProvider(I2CBus.BUS_1, 0x26);

		// provision gpio output pins and make sure they are all LOW at startup
		GpioPinDigitalOutput led_right[] = {
				gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B0, "com", PinState.LOW),
				gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B1, "torp", PinState.LOW),
				gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B2, "pod", PinState.LOW),
				gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B3, "red", PinState.LOW),
				gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B4, "yellow", PinState.LOW)
		};

		//buttons on right panel
		GpioPinDigitalInput buttons[] = {
				gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A7, "com", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A6, "torp", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A5, "pod", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A4, "red", PinPullResistance.PULL_UP),
				gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A3, "yellow", PinPullResistance.PULL_UP)
		};

		// create and register gpio pin listener
		gpio.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

				//tests for time between presses, simultaneous input and
				//proper switch position before processing the request.
				if (System.currentTimeMillis() > next_input && !processing_request &&
						event.getState() == PinState.HIGH) {
					processing_request = true;
					button_event(event, switch_speed, switch_amount);
					//turns off lights on right panel after handling the event
					gpio.setState(false, led_right);
					processing_request = false;
				}
			}
		}, buttons);

		//program variables
		//--------------------------------------------------------
		
		//enable button request processing
		processing_request = false;
		
		//sets the sound path
		path = "";

		//set current time as the next valid time for input
		next_input = System.currentTimeMillis();

		//set the index for the pod sound to enable right panel flashing
		active_alert_index = 2;

		//Initialize sound threads
		button_thread = new Thread(new Playmusic("button2.wav", 0));

		alert_thread = start_sound("pod.wav", 0, alert_thread);

		//main loop
		//----------------------------------------------------------------------
		while (true) {

			//calculates number of leds to change based on switch positions
			int number_to_change = count_on(switch_amount) + 1;

			//keeps track of the LEDs that have already been changed
			boolean[] changed = new boolean[9];

			//loop that changes random LEDs
			for (int counter = 0; counter < number_to_change; counter++) {

				//generate a random number from 0-8
				int index = (int) (Math.random() * 9);

				//increase index if it has already been changed until unchanged index is found
				while (changed[index]) {
					if (index == 8) index = 0;
					else index++;
				}

				//note change and toggle pin
				changed[index] = true;
				gpio.toggle(led_left[index]);
			}

			//toggles right panel LED if an alert sound is active
			if (alert_thread.isAlive()) gpio.toggle(led_right[active_alert_index]);

			//turns off LEDs if no sound thread is active
			else gpio.setState(false, led_right);

			//waits to loop based on the number of deactivated switches
			long rest_until = System.currentTimeMillis() + (count_on(switch_speed) * 100) + 100;
			while (rest_until > System.currentTimeMillis());
		}
	}

	//runs actions when right panel button is pressed
	static public void button_event(GpioPinDigitalStateChangeEvent event,
			GpioPinDigitalInput[] speed, GpioPinDigitalInput[] amount) {

		//plays a button sound
		button_sound();

		//file name for the sound to play
		String file_name = "";

		//loop_count is 0 to only play once, 1 to loop continuously
		//found_index is the active_alert_index for the pressed button
		int loop_count = 0, found_index = -1;

		//forking logic for each button
		switch (event.getPin().getName()) {
		case "com":
			file_name = path + "narration"; found_index = 0; break;
		case "torp":
			//torp button sets the sound path for alternate sounds
			set_path(index_switches(speed), index_switches(amount));
			file_name = path + "torp"; found_index = 1; break;
		case "pod":
			file_name = path + "pod"; found_index = 2; break;
		case "red":
			file_name = "redalert"; loop_count = 1; found_index = 3; break;
		case "yellow":
			file_name = "yellowalert"; loop_count = 1; found_index = 4; break;
		}

		//complete file name
		file_name = file_name + ".wav";

		//holds the results of a test to see if a sound is playing
		boolean active = alert_thread.isAlive();

		//if a sound is active, stop it.
		if (active) stop_sound(alert_thread);

		//start a new sound if the index is different than the sound that was just stopped
		//or if there was no active sound
		if (active_alert_index != found_index || !active) {
			active_alert_index = found_index;
			alert_thread = start_sound(file_name, loop_count, alert_thread);
		}
	}
	
	//set the path for the sound files
	static private void set_path(int speed, int amount) {
		path = MatchSwitches.match(speed, amount);
	}

	//starts a sound thread with the given file name and looping instruction
	static public Thread start_sound(String file_name, int loop_count, Thread t) {
		t = new Thread(new Playmusic(file_name, loop_count));
		t.start();
		return t;
	}

	//stop a passed sound thread
	static public void stop_sound(Thread t) {
		t.interrupt();
		while (t.isAlive());
	}

	//plays a button sound when right panel button or witch is pressed
	static public void button_sound() {

		//random number from 1-7 for the file name
		int sound_index = (int) (Math.random() * 7) + 1;

		//stop active sound
		if (button_thread.isAlive()) stop_sound(button_thread);

		//starts the button sound thread
		button_thread = start_sound("button"+sound_index+".wav", 0, button_thread);

		//calculates when the system should accept the next input
		next_input = System.currentTimeMillis() + BUTTON_DELAY;
	}

	//counts the number of switches found to be off in the given array of switches
	static public int count_on(GpioPinDigitalInput g[]) {
		int r = 0;
		for (GpioPinDigitalInput pin : g) if (pin.isHigh()) r++;
		return r;
	}
	
	//uses a bank of switches to generate a binary number
	//with a decimal value from 0 - 255
	//0 (all off) 00000000
	//255 (all on) 11111111
	//                                   128  64  32  16   8   4   2   1
	//5 (only third and first switch on)   0   0   0   0   0   1   0   1
	static public int index_switches(GpioPinDigitalInput g[]) {
		String value = "";

		//generates a string of 0s and 1s based on switch positions
		for (GpioPinDigitalInput pin : g) {
			if (pin.isHigh()) value = value + "1";
			else value = value + "0";
		}

		//converts string to base 2 int value
		return Integer.parseInt(value, 2);
	}
}
