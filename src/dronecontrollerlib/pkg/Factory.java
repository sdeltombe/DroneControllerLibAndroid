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
public abstract class Factory {
 
    protected Utility utility;
    
    public static final String MYO_TYPE = "MYO";
    
    public Factory(Utility utility)
    {
        this.utility = utility;
    }
    
    public Utility getUtility()
    {
        return utility;
    }
    
    public Controller createController(String type,Object[] args)
    {
        if(type == MYO_TYPE)
        {
            return new MyoController(getUtility(),args);
        }
        utility.trace("CreateController : Unknown type");
        return null;
    }
    
    public abstract Utility createUtility();

    
    
    
    
}
