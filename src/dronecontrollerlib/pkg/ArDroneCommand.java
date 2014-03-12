/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlib.pkg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Seb
 */



public class ArDroneCommand implements Cloneable {
    public DroneAction action;
    private float leftRightTilt;//tourner à gauche/tourner à droite
    private float frontBackTilt;//avant/arriere 
    private float verticalSpeed;//haut/bas
    private float angularSpeed;//rotation gauche/droite
    private static FloatBuffer fb;
    private static IntBuffer ib;
    
    
    public ArDroneCommand()
    {
        ByteBuffer bb = ByteBuffer.allocate(4);
        fb = bb.asFloatBuffer();
        ib = bb.asIntBuffer();
    }
    /* accesseur */
    
    public int getLeftRightTilt()
    {
        return intOfFloat(leftRightTilt);
    }
    public int getFrontBackTilt()
    {
        return intOfFloat(frontBackTilt);
    }
     public int getVerticalSpeed()
    {
        return intOfFloat(verticalSpeed);
    }
    public int getAngularSpeed()
    {
        return intOfFloat(angularSpeed);
    }
    
    /*mutateur*/
    
    public void setLeftRightTilt(float leftRightTilt)
    {
        this.leftRightTilt = controlValue(leftRightTilt);
    }
    public void setFrontBackTilt(float frontBackTilt)
    {
        this.frontBackTilt = controlValue(frontBackTilt);
    }
    public void setVerticalSpeed(float verticalSpeed) 
    {
        this.verticalSpeed = controlValue(verticalSpeed);
    }
    public void setAngularSpeed(float angularSpeed)
    {
        this.angularSpeed = controlValue(angularSpeed);
    }
    
    private float controlValue(float value)
    {
        if(value > 1)
        {
            return 1;
        }
        else if(value < -1)
        {
            return -1;
        }
        else{
            return value;
        }
    }
    
    
    @Override
    public ArDroneCommand clone()
    {
        ArDroneCommand cmd = new ArDroneCommand();
        cmd.leftRightTilt = leftRightTilt;
        cmd.frontBackTilt = frontBackTilt;
        cmd.verticalSpeed = verticalSpeed;
        cmd.angularSpeed = angularSpeed;
        cmd.action = action;
        return cmd;
    }
      
    public static int intOfFloat(float f) {
        fb.put(0, f);
        return ib.get(0);
    }

    
    public static float floatOfInt(int i) {
        ib.put(0, i);
        return fb.get(0);
    }
    
    @Override
    public String toString()
    {
        String chaine="action:" + action + "\n";
        chaine+="leftRightTilt:" + leftRightTilt + "\n";
        chaine+="frontBackTilt:" + frontBackTilt + "\n";
        chaine+="verticalSpeed:" + verticalSpeed + "\n";
        chaine+="angularSpeed:" + angularSpeed + "\n";
  
        return chaine;
    }
}
