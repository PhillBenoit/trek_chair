

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  MCP23017GpioExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2016 Pi4J
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
 * #L%
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

/**
 * <p>
 * This example code demonstrates how to setup a custom GpioProvider
 * for GPIO pin state control and monitoring.
 * </p>
 *
 * <p>
 * This example implements the MCP23017 GPIO expansion board.
 * More information about the board can be found here: *
 * http://ww1.microchip.com/downloads/en/DeviceDoc/21952b.pdf
 * </p>
 *
 * <p>
 * The MCP23017 is connected via I2C connection to the Raspberry Pi and provides
 * 16 GPIO pins that can be used for either digital input or digital output pins.
 * </p>
 *
 * @author Robert Savage
 */
public class MCP23017GpioExample {

    public static void main(String args[]) throws InterruptedException, UnsupportedBusNumberException, IOException {

        System.out.println("<--Pi4J--> MCP23017 GPIO Example ... started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
	System.out.println("got instance");

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

        // create and register gpio pin listener
        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                event_handler(event);
            }
        }, switch_speed);

        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                event_handler(event);
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
            gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_A7, "circle-1", PinState.LOW),
            gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_A6, "circle-2", PinState.LOW),
            gpio.provisionDigitalOutputPin(left_leds, MCP23017Pin.GPIO_A5, "circle-3", PinState.LOW)
         };

	//right pannel----------------------------------------------------------------------
	final MCP23017GpioProvider right = new MCP23017GpioProvider(I2CBus.BUS_1, 0x26);

	// provision gpio output pins and make sure they are all LOW at startup
        GpioPinDigitalOutput led_right[] = {
            gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B0, "circle-1r", PinState.LOW),
            gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B1, "circle-2r", PinState.LOW),
            gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B2, "circle-3r", PinState.LOW),
            gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B3, "circle-4r", PinState.LOW),
            gpio.provisionDigitalOutputPin(right, MCP23017Pin.GPIO_B4, "circle-5r", PinState.LOW)
         };

        GpioPinDigitalInput buttons[] = {
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A7, "button-1", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A6, "button-2", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A5, "button-3", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A4, "button-4", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(right, MCP23017Pin.GPIO_A3, "button-5", PinPullResistance.PULL_UP),
          };

        // create and register gpio pin listener
        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                event_handler(event);
            }
        }, buttons);


        // keep program running for 20 seconds----------------------------------------------------------------------
        for (int count = 0; count < 10; count++) {
            gpio.setState(true, led_left);
            Thread.sleep(1000);
            gpio.setState(false, led_left);
            Thread.sleep(1000);
        }

	show_off(switch_speed);
	show_off(switch_amount);

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting MCP23017GpioExample");
    }

	static public void event_handler(GpioPinDigitalStateChangeEvent event) {
		System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = "
                        + event.getState());
	}

	static public void show_off(GpioPinDigitalInput g[]) {
		for (GpioPinDigitalInput pin : g) if (pin.isLow())
			System.out.println(pin.getName());
	}

}

