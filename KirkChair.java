

/*
 * **********************************************************************
 * This file is modiefied from part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2016 Pi4J & 2017 Star Trek Tucson Fan Club
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

    public static void main(String args[]) throws InterruptedException, UnsupportedBusNumberException, IOException {

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // create custom MCP23017 GPIO provider left switches----------------------------------------------------------------------
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
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B0, "red-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B1, "yellow-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B2, "white-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B3, "blue-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B4, "sky_blue-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B5, "powder_blue-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B6, "grey-amount", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(left_switches, MCP23017Pin.GPIO_B7, "dark_grey-amount", PinPullResistance.PULL_UP)
          };

        // create and register gpio pin listeners
        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                switch_event(event);
            }
        }, switch_speed);

        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                switch_event(event);
            }
        }, switch_amount);


	//left lights----------------------------------------------------------------------
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

	//right pannel----------------------------------------------------------------------
	final MCP23017GpioProvider right = new MCP23017GpioProvider(I2CBus.BUS_1, 0x26);

	// provision gpio output pins and make sure they are all LOW at startup
        GpioPinDigitalOutput led_right[] = {
		gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B0, "com", PinState.LOW),
        	gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B1, "blank", PinState.LOW),
        	gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B2, "pod", PinState.LOW),
        	gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B3, "yellow", PinState.LOW),
        	gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B4, "red", PinState.LOW)
	};

	//buttons on right pannel
        GpioPinDigitalInput buttons[] = {
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A7, "com", PinPullResistance.PULL_UP),
                //gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A6, "blank", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A5, "pod", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A4, "red", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A3, "yellow", PinPullResistance.PULL_UP)
        };

        // create and register gpio pin listener
        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                button_event(event);
		//turns off lights on right pannel after handling the event
		gpio.setState(false, led_right);
            }
        }, buttons);

        //initalize sound threads--------------------------------------------------------
	button_thread = new Thread(new Playmusic("button2.wav", 0));
	button_thread.start();
	alert_thread = new Thread(new Playmusic("pod.wav", 0));
	alert_thread.start();
	active_alert_index = 2;

	//main loop----------------------------------------------------------------------
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

	    //toggles right pannel LED if an alert sound is active
	    if (alert_thread.isAlive()) gpio.toggle(led_right[active_alert_index]);

	    //turns off LEDs if no sound thread is active
	    else gpio.setState(false, led_right);

	    //sleeps main thread based on the number of deactivated switches
	    Thread.sleep((count_on(switch_speed) * 100) + 100);
        }
    }

	//threads for sounds
	static private Thread button_thread, alert_thread;

	//array index for the light with active sound on the right side
	static private int active_alert_index;

	//plays a button sound when a switch is turned off
	static public void switch_event(GpioPinDigitalStateChangeEvent event) {
	    //exclusive test for off event
	    if (event.getState() == PinState.LOW) button_sound();
	}

	//runs actions when right pannel button is pressed
	static public void button_event(GpioPinDigitalStateChangeEvent event) {
	    //exclusive test for on event
	    if (event.getState() == PinState.HIGH) {

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
			file_name = "narration"; found_index = 0; break;
		    case "pod":
			file_name = "pod"; found_index = 2; break;
		    case "red":
			file_name = "redalert"; loop_count = 1; found_index = 4; break;
		    case "yellow":
			file_name = "yellowalert"; loop_count = 1; found_index = 3; break;
		}

		//complete file name
		file_name = file_name + ".wav";

		//holds the redults of a test to see if a sound is playing
		boolean active = alert_thread.isAlive();

		//if a sound is active, stop it.
		if (active) {
			alert_thread.interrupt();
			while (alert_thread.isAlive());
		}

		//start a new sound if the index is different than the sound that was just stopped
		//or if there was no active sound
		if (active_alert_index != found_index || !active) {
			active_alert_index = found_index;
			alert_sound(file_name, loop_count);
		}
	    }
	}


	//starts a sound thread with the given file name and looping instruction
	static public void alert_sound(String file_name, int loop_count) {
		alert_thread = new Thread(new Playmusic(file_name, loop_count));
		alert_thread.start();
	}

	//plays a button sound when right pannel button or witch is pressed
	static public void button_sound() {

		//atempt to stop an already active button sound
		if (button_thread.isAlive()) {
			button_thread.interrupt();
			while (button_thread.isAlive());
		}

		//random number from 1-7 for the file name
		int index = (int) (Math.random() * 7) + 1;

		//starts the button sound thread
		button_thread = new Thread(new Playmusic("button"+index+".wav", 0));
		button_thread.start();
	}

	//counts the number of switches found to be off in the given array of switches
	static public int count_on(GpioPinDigitalInput g[]) {
		int r = 0;
		for (GpioPinDigitalInput pin : g) if (pin.isHigh()) r++;
		return r;
	}

}
