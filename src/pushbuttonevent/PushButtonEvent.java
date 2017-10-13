/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pushbuttonevent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author yjkim
 */
public class PushButtonEvent implements Runnable {
    private static final String LED_PIN = "GPIO18";
    private static final String LED_BTN_PIN = "GPIO23";
    private static final String EXIT_BTN_PIN = "GPIO24";
    
    private GPIOPin ledPin = null;
    private GPIOPin ledBtnPin = null;
    private GPIOPin exitBtnPin = null;
    private volatile boolean exit = false;

    public PushButtonEvent() throws IOException {
        ledBtnPin = DeviceManager.open(LED_BTN_PIN, GPIOPin.class);
        exitBtnPin = DeviceManager.open(EXIT_BTN_PIN, GPIOPin.class);
        ledPin = DeviceManager.open(LED_PIN, GPIOPin.class);
//
/*
        GPIOPinConfig pinConfig = new GPIOPinConfig(DeviceConfig.DEFAULT,
                                                    18,
                                                    GPIOPinConfig.DIR_OUTPUT_ONLY,
                                                    GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
                                                    GPIOPinConfig.TRIGGER_NONE,
                                                    false);
        ledPin = (GPIOPin)DeviceManager.open(GPIOPin.class, pinConfig);
*/        
        System.out.println("Devices were successfully opened in constructor...");
 
        ledBtnPin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                try {
                    boolean led = pe.getValue();
                    ledPin.setValue(led);
                    System.out.println("LED: " + (led ? " ON" : "OFF"));
                }
                catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    exit = true;
                }
            }            
        });
        
        exitBtnPin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                if (!exit) {
                    exit = pe.getValue();
                }
            }            
        });
    }
    
    public void close() throws IOException {
        ledPin.close();
        ledBtnPin.close();
        exitBtnPin.close();
    }
        
    public void run() {
        try {
            while (!exit) {
                Thread.sleep(500);
            }
            close();
        } catch (InterruptedException ex) {
            Logger.getLogger(PushButtonEvent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PushButtonEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Exit...");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        PushButtonEvent pbe = new PushButtonEvent();
        Thread thread = new Thread(pbe);
        thread.start();
    }
    
}
