/*
 * 
 * Test program that displays indexing totals for switch positions.
 * Used for writing values in to the MatchSwitches object. 
 * 
 */

import java.io.IOException;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
//import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class SwitchTest {

	public static void main(String[] args) throws UnsupportedBusNumberException, IOException {

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		// create MCP23017 GPIO provider left switches
		//----------------------------------------------------------------------
		final MCP23017GpioProvider left_switches = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);


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

		//event change listeners
		//-------------------------------
		gpio.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				run_change(switch_speed, switch_amount);
			}
		}, switch_amount);
		
		gpio.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				run_change(switch_speed, switch_amount);
			}
		}, switch_speed);

	}
	
	//displays totals when a switch position changes
	static private void run_change(GpioPinDigitalInput[] switch_speed, GpioPinDigitalInput[] switch_amount) {
		String output_string = "Value of %s: %d\n";
		System.out.println("----------------------");
		System.out.format(output_string, "speed", index_switches(switch_speed));
		System.out.format(output_string, "amount", index_switches(switch_amount));
		System.out.println("----------------------");
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