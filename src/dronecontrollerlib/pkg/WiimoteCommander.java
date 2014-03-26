/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dronecontrollerlib.pkg;

import static dronecontrollerlib.pkg.DroneAction.*;

/**
 *
 * @author Mathieu
 */
public class WiimoteCommander {

    Controller controller;
    boolean etatModeAccelero = false;
    boolean etatAncienBoutonHomeAccelero = false;
    float ancienneValeurX;
    
    public WiimoteCommander (Controller controller)
    {
        this.controller = controller;
    }
    
    public void onReceivedEvent (WiimoteData wiimoteData)
    {
        if (wiimoteData.etatBouton1 == true)
        {
            controller.utility.trace ("==TAKE OFF==");
            controller.cmd.action=TAKE_OFF;
        }
        else if (wiimoteData.etatBouton2 == true)
        {
            controller.utility.trace ("==LANDING==");
            controller.cmd.action=LANDING;
        }
        else if (wiimoteData.etatBoutonHome != etatAncienBoutonHomeAccelero && wiimoteData.etatBoutonHome == true)
        {
            if (etatModeAccelero == true)
            {
                controller.utility.trace ("==MODE ACC DESACTIVE==");
                etatModeAccelero = false;
            }
            else if (etatModeAccelero == false)
            {
                controller.utility.trace ("==MODE ACC ACTIVE==");
                etatModeAccelero = true;
            }
        }
        else if (etatModeAccelero == true)
        {
            modeAccelero (wiimoteData);
        }
        else if (etatModeAccelero == false)
        {
            modeNormal (wiimoteData);
        }
        etatAncienBoutonHomeAccelero = wiimoteData.etatBoutonHome; 
        //controller.notifySubscriber();

    }
    
    
    ///////////////////// MODE ACELERO ///////////////////////////////
    private void modeAccelero (WiimoteData wiimoteData) {
        if (wiimoteData.etatBoutonA == true)
        {
            configureCommand ("==AVANCE==",(float) 0,(float) 0, (float) -0.5, (float) 0);
        }
        else if (wiimoteData.etatBoutonB == true)
        {
            configureCommand ("==RECULE==",(float) 0,(float) 0, (float) 0.5, (float) 0);
        }
        else if (wiimoteData.etatBoutonDroit == true)
        {
            configureCommand ("==DROITE==",(float) 0,(float) 0.5, (float) 0, (float) 0);
        }
        else if (wiimoteData.etatBoutonGauche == true)
        {
            configureCommand ("==GAUCHE==",(float) 0,(float) -0.5, (float) 0, (float) 0);
        }
        else if ((wiimoteData.anglePitch >= (float)0.2 || wiimoteData.anglePitch <= (float)-0.1) 
                && wiimoteData.anglePitch != ancienneValeurX)
        {
            configureCommand ("==DEPLACEMENT AV/AR==",(float) 0,(float) 0, wiimoteData.anglePitch, (float) 0);
           // ancienneValeurX = 
        }
        else if (wiimoteData.angleRoll >= (float)0.2 || wiimoteData.angleRoll <= (float)-0.1)
        {
            configureCommand ("==DEPLACEMENT G/D==",(float) 0,wiimoteData.angleRoll, (float) 0, (float) 0);
        }
        else if (wiimoteData.etatBoutonPos == true)
        {
            configureCommand ("==MONTE==",(float) 0.5,(float) 0, (float) 0, (float) 0);
        }
        else if (wiimoteData.etatBoutonNeg == true)
        {
            configureCommand ("==DESCEND==",(float) -0.5,(float) 0, (float) 0, (float) 0);
        }
        else 
        {
            controller.utility.trace ("==HOVERING==");
            controller.cmd.action = HOVERING;
        }
    }
    
    ////////////////////////// MODE NORMAL ///////////////////////////////
    private void modeNormal (WiimoteData wiimoteData) {
        if (wiimoteData.etatBoutonA == true)
        {
            configureCommand ("==AVANCE==",(float) 0,(float) 0, (float) -0.5, (float) 0);
        }
        else if (wiimoteData.etatBoutonB == true)
        {
            configureCommand ("==RECULE==",(float) 0,(float) 0, (float) 0.5, (float) 0);
        }
        else if (wiimoteData.etatBoutonDroit == true)
        {
            configureCommand ("==DROITE==",(float) 0,(float) 0.5, (float) 0, (float) 0);
        }
        else if (wiimoteData.etatBoutonGauche == true)
        {
            configureCommand ("==GAUCHE==",(float) 0,(float) -0.5, (float) 0, (float) 0);
        }
        else if (wiimoteData.etatBoutonPos == true)
        {
            configureCommand ("==MONTE==",(float) 0.5,(float) 0, (float) 0, (float) 0);
        }
        else if (wiimoteData.etatBoutonNeg == true)
        {
            configureCommand ("==DESCEND==",(float) -0.5,(float) 0, (float) 0, (float) 0);
        }
        else 
        {
            controller.utility.trace ("==HOVERING==");
            controller.cmd.action = HOVERING;
        }
    }
    
    private void configureCommand (String str, float verticalSpeed, float angularSpeed, float frontBackTilt, float leftRightTilt)
    {
        controller.utility.trace (str);
        controller.cmd.setVerticalSpeed(verticalSpeed);
        controller.cmd.setAngularSpeed(angularSpeed);
        controller.cmd.setFrontBackTilt(frontBackTilt);
        controller.cmd.setLeftRightTilt(leftRightTilt);
        controller.cmd.action = MOVING; 
    }

}
