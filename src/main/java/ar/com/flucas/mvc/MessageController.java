/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ar.com.flucas.mvc;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import com.pi4j.wiringpi.SoftTone;

@Controller
@RequestMapping("/")
public class MessageController {
	
	@RequestMapping()
	public ModelAndView list() {
		
		return new ModelAndView("layout", "messages", "");
	}

	@RequestMapping("/led")
    public String led(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        
		GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "LED", PinState.HIGH);
                
        try {
        	ledPin.low();
			Thread.sleep(2*1000);
			ledPin.high();
			Thread.sleep(2*1000);
			ledPin.low();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			gpio.shutdown();
			gpio.unprovisionPin(ledPin);
		}
                
		model.addAttribute("name", name);
        
		return "hello";
    }
	
	@RequestMapping("/hello")
    public String hello(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        
		int PIEZO_PIN = 3;
		
		GpioController gpio = GpioFactory.getInstance();
		
		SoftTone.softToneCreate(PIEZO_PIN);
		
		int [] scale = { 659, 659, 0, 659, 0, 523, 659, 0, 784, 0, 0, 0, 392, 0, 0, 0, 523, 0, 0, 392, 0, 0, 330 };
		
		for (int i = 0; i < 23; ++i)
        {
			try {
				SoftTone.softToneWrite(PIEZO_PIN, scale[i]);
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
        }
        
		gpio.shutdown();
		SoftTone.softToneStop(PIEZO_PIN);
		
		model.addAttribute("name", name);
		return "hello";
    }

}
