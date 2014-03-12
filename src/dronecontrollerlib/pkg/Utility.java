/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlib.pkg;

/**
 *
 * @author Seb
 */
public abstract class Utility {
    public enum TraceLevel
    {
        INFO,
        DEBUG
    }
    public abstract void trace(String trace);
    public abstract void trace(String trace, TraceLevel level);
    public abstract void traceError(String trace,Exception ex);
    public void threadSleep(int milliseconds)
    {
        try{
              Thread.sleep(milliseconds);
        }catch(InterruptedException exInterrupted)
        {
           this.traceError("InterruptedIOException during receive message", exInterrupted);
        }
    }
    
   
}
