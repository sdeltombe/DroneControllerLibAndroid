/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlibandroid.pkg;
import android.app.Activity;
import android.content.Context;
import dronecontrollerlib.pkg.Utility;
import dronecontrollerlib.pkg.Utility.TraceLevel;
import android.util.*;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Seb
 */
public class AndroidUtility extends Utility {

    private Activity view;
    private List<String> histoToast = new ArrayList<String>();
    private static final int histoSize = 10;
    private boolean makeText = false;
    Toast toast;
    ToastCanceler toastCancel = new ToastCanceler();
    //ToastMessageTask toast;
    public void SetView(Activity view)
    {
        
        this.view = view;
       //toast.SetActivity(view);
    }
    
    private void addToastHisto(String trace)
    {
        if(histoToast.size()<histoSize)
        {
            histoToast.add(trace);
        }else{
            histoToast.remove(0);
            histoToast.add(trace);
        }
    }
    
    public void EndToast()
    {
        toastCancel.end();
    }
    
    public void printToastHisto()
    {
        
            Context context = view.getApplicationContext();
            
            String chaine = new String();
            for(String s : histoToast)
            {
                chaine += s + "\n";
            }
            
            CharSequence text = chaine;

            if(!makeText)
            {
                makeText = true;
                toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toastCancel.SetToast(toast);
                toastCancel.SetUtility(this);
                toastCancel.SetView(view);
                toastCancel.Cancel();
                toastCancel.start();
            }else {
                toastCancel.Cancel();
                toast.setText(text);
            }
    }
    
    public void toast(String trace)
    {
       
        addToastHisto(trace);
        printToastHisto();
    }
    
    public void trace(String trace) {
        Log.v("DRONE_CONTROLLER",trace);
        if(view != null)
        {
            //toast(trace);
        }
    }
    
    public void traceError(String trace, Exception ex) {
        trace(trace);
        String chaine = new String();
        for( StackTraceElement ste : ex.getStackTrace())
        {
            chaine+=ste + "\n";
        }
        trace(chaine);
        
    }

    public void trace(String trace, TraceLevel level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
     
}
