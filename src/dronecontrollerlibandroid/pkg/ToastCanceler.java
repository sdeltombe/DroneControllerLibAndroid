/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlibandroid.pkg;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;
import dronecontrollerlib.pkg.Utility;

/**
 *
 * @author Seb
 */

public class ToastCanceler extends Thread {
    
    private Toast toast;
    private Boolean end=false;
    private Boolean cancel=false;
    private Utility utility;
    private Activity view;
    
    public void Cancel()
    {
        cancel = true;
    }
    
    public void SetToast(Toast toast)
    {
        this.toast = toast;
    }
    public void SetUtility(Utility utility)
    {
        this.utility = utility;
    }
    public void SetView(Activity view)
    {
        this.view = view;
    }
    public void end()
    {
        end = true;
        
    }
    
    @Override
    public void start()
    {
            super.start();
    }
    
     @Override
    public void run() {
            while(!end)
            {
                utility.threadSleep(200);
                synchronized(toast)
                {
                    //if(cancel)
                //{
                    toast.cancel();
                //}
               
                    view.runOnUiThread(new Runnable() 
                    {
                   
                    public void run() {
              
                        //if(cancel)
                        //{
                            
                        toast.show();
                           // cancel = false;
                        //}
                        
                    }
                   });
                }
            }
            toast.cancel();
    }
    
    
   

 
}
