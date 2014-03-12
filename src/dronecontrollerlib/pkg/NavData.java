/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlib.pkg;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Seb
 */
public class NavData {
   
    private final Map<String,Boolean> states = new HashMap<String,Boolean>();
    private Boolean update = false;
    public static final String FLYING = "flying";
    public static final String PB_COM="communicationProblemOccurred";
    public NavData()
    {
        states.put(FLYING,false);
        states.put("videoEnabled",false);
        states.put("visionEnabled",false);
        states.put("controlAlgorithm",false);
        states.put("altitudeControlActive",false);
        states.put("userFeedbackOn",false);
        states.put("controlReceived",false);
        states.put("trimReceived",false);
        states.put("trimRunning",false);
        states.put("trimSucceeded",false);
        states.put("navDataDemoOnly",false);
        states.put("navDataBootstrap",false);
        states.put("motorsDown",false);
        states.put("gyrometersDown",false);
        states.put("batteryTooLow",false);
        states.put("batteryTooHigh",false);
        states.put("timerElapsed",false);
        states.put("notEnoughPower",false);
        states.put("angelsOutOufRange",false);
        states.put("tooMuchWind",false);
        states.put("ultrasonicSensorDeaf",false);
        states.put("cutoutSystemDetected",false);
        states.put("PICVersionNumberOK",false);
        states.put("ATCodedThreadOn",false);
        states.put("navDataThreadOn",false);
        states.put("videoThreadOn",false);
        states.put("acquisitionThreadOn",false);
        states.put("controlWatchdogDelayed",false);
        states.put("ADCWatchdogDelayed",false);
        states.put(PB_COM,false);
        states.put("emergency",false);
    }
    public Boolean IsUpdate()
    {
        return update;
    }
    private void SetState(String stateType, Boolean value)
    {
        if(states.get(stateType) != value)
        {
            update = true;
            states.put(stateType, value);
        }
    }
    public Boolean getState(String stateType)
    {
        if(states.containsKey(stateType))
        {
            return states.get(stateType);
        }else{
            return false;
        }
    }
    public void parseNavData(int state)
    {
        update = false;
        SetState("flying" , (state & 1) != 0);
        SetState("videoEnabled" , (state & (1 << 1)) != 0);
        SetState("visionEnabled" , (state & (1 << 2)) != 0);
        //this.controlAlgorithm = (state & (1 << 3)) != 0 ? ControlAlgorithm.ANGULAR_SPEED_CONTROL
        //        :ControlAlgorithm.EULER_ANGELS_CONTROL;
        SetState("altitudeControlActive" , (state & (1 << 4)) != 0);
        SetState("userFeedbackOn" , (state & (1 << 5)) != 0);
        SetState("controlReceived" , (state & (1 << 6)) != 0);
        SetState("trimReceived" , (state & (1 << 7)) != 0);
        SetState("trimRunning" , (state & (1 << 8)) != 0);
        SetState("trimSucceeded" , (state & (1 << 9)) != 0);
        SetState("navDataDemoOnly" , (state & (1 << 10)) != 0);
        SetState("navDataBootstrap" , (state & (1 << 11)) != 0);
        SetState("motorsDown" , (state & (1 << 12)) != 0);
        SetState("gyrometersDown" , (state & (1 << 14)) != 0);
        SetState("batteryTooLow" , (state & (1 << 15)) != 0);
        SetState("batteryTooHigh" , (state & (1 << 16)) != 0);
        SetState("timerElapsed" , (state & (1 << 17)) != 0);
        SetState("notEnoughPower" , (state & (1 << 18)) != 0);
        SetState("angelsOutOufRange" , (state & (1 << 19)) != 0);
        SetState("tooMuchWind" , (state & (1 << 20)) != 0);
        SetState("ultrasonicSensorDeaf" , (state & (1 << 21)) != 0);
        SetState("cutoutSystemDetected" , (state & (1 << 22)) != 0);
        SetState("PICVersionNumberOK" , (state & (1 << 23)) != 0);
        SetState("ATCodedThreadOn" , (state & (1 << 24)) != 0);
        SetState("navDataThreadOn" , (state & (1 << 25)) != 0);
        SetState("videoThreadOn" , (state & (1 << 26)) != 0);
        SetState("acquisitionThreadOn" , (state & (1 << 27)) != 0);
        SetState("controlWatchdogDelayed" , (state & (1 << 28)) != 0);
        SetState("ADCWatchdogDelayed" , (state & (1 << 29)) != 0);
        SetState("communicationProblemOccurred" , (state & (1 << 30)) != 0);
        SetState("emergency" , (state & (1 << 31)) != 0);
    }
    @Override
    public String toString()
    {
        String chaine="===NavData===\n"; 
        for(Entry<String,Boolean> entry : states.entrySet())
        {
            if(entry.getValue())
            {
                chaine+=entry.getKey() + "\n";
            }
        }
        return chaine; 
    }
    
    
}
