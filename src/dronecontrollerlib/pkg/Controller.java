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
public abstract class Controller {
    
    /* Private member */
    private ISubscriber subscriber;
    
    /* Protected member */
    protected ArDroneCommand cmd;
    protected Utility utility;
    protected Object[] args;
    
    /*public member */
    
    
    public Controller(Utility utility,Object[] args)
    {
        this.utility = utility;
        this.args=args;
        cmd = new ArDroneCommand();
    }
    
    
    public abstract void connect();
    public abstract void listen();
    public abstract void disconnect();
    public void subscribe(ISubscriber subscriber)
    {
        this.subscriber = subscriber;
    }
    public void notifySubscriber()
    {
        this.subscriber.onReceivedCommand(cmd);
    }
  
    
            
            
}
