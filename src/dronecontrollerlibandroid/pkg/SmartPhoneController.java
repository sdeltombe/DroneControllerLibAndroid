/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlibandroid.pkg;
import android.app.Activity;
import dronecontrollerlib.pkg.Controller;
import dronecontrollerlib.pkg.DroneAction;
import dronecontrollerlib.pkg.Utility;
/**
 *
 * @author Seb
 */
public class SmartPhoneController extends Controller implements SPListener  {
    
    CustomEventActivity view;
    public SmartPhoneController(Utility utility,Object[] args) {
        super(utility,args);
        view = (CustomEventActivity)args[0];
        
    }

    @Override
    public void connect() {
         view.SetListener(this);
    }

    @Override
    public void listen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disconnect() {
        view.SetListener(null);
    }

    public void TakeOff() {
        utility.trace("Takeoff !!!");
        cmd.action = DroneAction.TAKE_OFF;
        notifySubscriber();
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void Landing() {
        utility.trace("Landing !!!");
        cmd.action = DroneAction.LANDING;
        notifySubscriber();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
