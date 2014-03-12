/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlibandroid.pkg;
import dronecontrollerlib.pkg.Factory;
import dronecontrollerlib.pkg.Controller;
import dronecontrollerlib.pkg.Utility;
/**
 *
 * @author Seb
 */
public class AndroidFactory extends Factory{

    public static final String SMARTPHONE_TYPE = "SMARTPHONE";
    
    public AndroidFactory(Utility utility) {
        super(utility);
    }
   
    @Override
    public Controller createController(String type,Object[] args)
    {
        if(type == SMARTPHONE_TYPE)
        {
            return new SmartPhoneController(utility,args);
        }
        
        return super.createController(type,args);
    }


    public Utility createUtility() {
        return new AndroidUtility();
    }
    
}
