/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlibandroid.pkg;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.Toast;
import dronecontrollerlib.pkg.*;
/**
 *
 * @author Seb
 */
public class WiimoteController extends Controller {

    
   //These constants are copied from the BluezService
	public static final String SESSION_ID = "com.hexad.bluezime.sessionid";
	
	public static final String EVENT_KEYPRESS = "com.hexad.bluezime.keypress";
	public static final String EVENT_KEYPRESS_KEY = "key";
	public static final String EVENT_KEYPRESS_ACTION = "action";

	public static final String EVENT_DIRECTIONALCHANGE = "com.hexad.bluezime.directionalchange";
	public static final String EVENT_DIRECTIONALCHANGE_DIRECTION = "direction";
	public static final String EVENT_DIRECTIONALCHANGE_VALUE = "value";

	public static final String EVENT_CONNECTED = "com.hexad.bluezime.connected";
	public static final String EVENT_CONNECTED_ADDRESS = "address";

	public static final String EVENT_DISCONNECTED = "com.hexad.bluezime.disconnected";
	public static final String EVENT_DISCONNECTED_ADDRESS = "address";

	public static final String EVENT_ERROR = "com.hexad.bluezime.error";
	public static final String EVENT_ERROR_SHORT = "message";
	public static final String EVENT_ERROR_FULL = "stacktrace";
	
	public static final String REQUEST_STATE = "com.hexad.bluezime.getstate";

	public static final String REQUEST_CONNECT = "com.hexad.bluezime.connect";
	public static final String REQUEST_CONNECT_ADDRESS = "address";
	public static final String REQUEST_CONNECT_DRIVER = "driver";
	
	public static final String REQUEST_DISCONNECT = "com.hexad.bluezime.disconnect";
	
	public static final String EVENT_REPORTSTATE = "com.hexad.bluezime.currentstate";
	public static final String EVENT_REPORTSTATE_CONNECTED = "connected";
	public static final String EVENT_REPORTSTATE_DEVICENAME = "devicename";
	public static final String EVENT_REPORTSTATE_DISPLAYNAME = "displayname";
	public static final String EVENT_REPORTSTATE_DRIVERNAME = "drivername";
	
	public static final String REQUEST_FEATURECHANGE = "com.hexad.bluezime.featurechange";
	public static final String REQUEST_FEATURECHANGE_RUMBLE = "rumble"; //Boolean, true=on, false=off
	public static final String REQUEST_FEATURECHANGE_LEDID = "ledid"; //Integer, LED to use 1-4 for Wiimote
	public static final String REQUEST_FEATURECHANGE_ACCELEROMETER = "accelerometer"; //Boolean, true=on, false=off
	
	public static final String REQUEST_CONFIG = "com.hexad.bluezime.getconfig";
	
	public static final String EVENT_REPORT_CONFIG = "com.hexad.bluezime.config";
	public static final String EVENT_REPORT_CONFIG_VERSION = "version";
	public static final String EVENT_REPORT_CONFIG_DRIVER_NAMES = "drivernames";
	public static final String EVENT_REPORT_CONFIG_DRIVER_DISPLAYNAMES = "driverdisplaynames";
        
        public static final String EVENT_ACCELEROMETERCHANGE = "com.hexad.bluezime.accelerometerchange";
	public static final String EVENT_ACCELEROMETERCHANGE_AXIS = "axis";
	public static final String EVENT_ACCELEROMETERCHANGE_VALUE = "value";       
	
	private static final String BLUEZ_IME_PACKAGE = "com.hexad.bluezime";
	private static final String BLUEZ_IME_SERVICE = "com.hexad.bluezime.BluezService";
	
	//These are from API level 9
	public static final int KEYCODE_BUTTON_A = 29;
	public static final int KEYCODE_BUTTON_B = 30;
        public static final int KEYCODE_BUTTON_PLUS = 81;
        public static final int KEYCODE_BUTTON_MOINS = 69;
        public static final int KEYCODE_BUTTON_HOME = 36;
        public static final int KEYCODE_BUTTON_1 = 8;
        public static final int KEYCODE_BUTTON_2 = 9;
        public static final int KEYCODE_BUTTON_HAUT = 19;
        public static final int KEYCODE_BUTTON_BAS = 20;
        public static final int KEYCODE_BUTTON_GAUCHE = 21;
        public static final int KEYCODE_BUTTON_DROITE = 22;
	

	//A string used to ensure that apps do not interfere with each other
	public static final String SESSION_NAME = "TEST-BLUEZ-IME";
    
    private Activity activity;
    private Boolean m_connected;
    
    private WiimoteCommander exeProg;
    private boolean activateAccData=false;
    private boolean xReceived = false;
    private int xValue;
    
    public WiimoteController(Utility utility,Object[] args) {
        super(utility,args);
        utility.trace("WiimoteController instanciate !!!");
        activity = (Activity)args[0];
        exeProg = new WiimoteCommander(this);
       
        
    }
    
    @Override
    public void connect() {
         Intent serviceIntent = new Intent(REQUEST_CONNECT);
         serviceIntent.setClassName(BLUEZ_IME_PACKAGE, BLUEZ_IME_SERVICE);
         serviceIntent.putExtra(SESSION_ID, SESSION_NAME);
	 serviceIntent.putExtra(REQUEST_CONNECT_ADDRESS, "00:1E:35:0F:DA:E1");
	 serviceIntent.putExtra(REQUEST_CONNECT_DRIVER, "wiimote");
	 activity.startService(serviceIntent);
         
         //Request config, not present in version < 9
        serviceIntent = new Intent(REQUEST_CONFIG);
        serviceIntent.setClassName(BLUEZ_IME_PACKAGE, BLUEZ_IME_SERVICE);
        serviceIntent.putExtra(SESSION_ID, SESSION_NAME);
        activity.startService(serviceIntent); 
        
        //Request device connection state
        serviceIntent = new Intent(REQUEST_STATE);
        serviceIntent.setClassName(BLUEZ_IME_PACKAGE, BLUEZ_IME_SERVICE);
        serviceIntent.putExtra(SESSION_ID, SESSION_NAME);
        activity.startService(serviceIntent);
        
         //activateorNotAcc(true);
    }

    @Override
    public void listen() {
        activity.registerReceiver(stateCallback, new IntentFilter(EVENT_REPORT_CONFIG));
        activity.registerReceiver(stateCallback, new IntentFilter(EVENT_REPORTSTATE));
        activity.registerReceiver(stateCallback, new IntentFilter(EVENT_CONNECTED));
        activity.registerReceiver(stateCallback, new IntentFilter(EVENT_DISCONNECTED));
        activity.registerReceiver(stateCallback, new IntentFilter(EVENT_ERROR));
        
        activity.registerReceiver(statusMonitor, new IntentFilter(EVENT_DIRECTIONALCHANGE));
        activity.registerReceiver(statusMonitor, new IntentFilter(EVENT_KEYPRESS));
        activity.registerReceiver(statusMonitor, new IntentFilter(EVENT_ACCELEROMETERCHANGE));
        activity.registerReceiver(statusMonitor, new IntentFilter(EVENT_ACCELEROMETERCHANGE_VALUE));
        activity.registerReceiver(statusMonitor, new IntentFilter(EVENT_ACCELEROMETERCHANGE_AXIS));
    }

    @Override
    public void disconnect() {
         Intent serviceIntent = new Intent(REQUEST_DISCONNECT);
	 serviceIntent.setClassName(BLUEZ_IME_PACKAGE, BLUEZ_IME_SERVICE);
	 serviceIntent.putExtra(SESSION_ID, SESSION_NAME);
	 activity.startService(serviceIntent);
    }
    
    private void activateorNotAcc(boolean value)
    {
         Intent serviceIntent = new Intent(REQUEST_FEATURECHANGE);
	 serviceIntent.setClassName(BLUEZ_IME_PACKAGE, BLUEZ_IME_SERVICE);
         serviceIntent.putExtra(SESSION_ID, SESSION_NAME);
         if(value)
            serviceIntent.putExtra(REQUEST_FEATURECHANGE_ACCELEROMETER, true);
	 else
            serviceIntent.putExtra(REQUEST_FEATURECHANGE_ACCELEROMETER, false); 
         
         boolean test = serviceIntent.getBooleanExtra(REQUEST_FEATURECHANGE_ACCELEROMETER, false);
         utility.trace("getBooleanExtra:" + test);
         activity.startService(serviceIntent);
    }
    private WiimoteData onReceivedAccData(int x, int y)
    {
        utility.trace("x:" + x + " y:" + y);
        WiimoteData wdata = new WiimoteData();
        //wdata.anglePitch = x;
        wdata.angleRoll = y;
        return wdata;
    }
    private WiimoteData onReceivedBtCommand(int bt, int value)
    {
        WiimoteData wdata = new WiimoteData();
        switch (bt){
            case KEYCODE_BUTTON_A : 
                    utility.trace("bt A");
                    if(value==0)
                        wdata.etatBoutonA = true;
                    else
                        wdata.etatBoutonA = false;
                    break;
            case KEYCODE_BUTTON_B :  
                    utility.trace("bt B");
                    if(value==0)
                        wdata.etatBoutonB = true;
                    else
                        wdata.etatBoutonB = false;
                    break;
            case KEYCODE_BUTTON_PLUS :  
                    utility.trace("bt PLUS");
                    if(value==0)
                        wdata.etatBoutonPos = true;
                    else
                        wdata.etatBoutonPos = false;
                    break;
            case KEYCODE_BUTTON_MOINS :  
                    utility.trace("bt MOINS");
                    if(value==0)
                        wdata.etatBoutonNeg = true;
                    else
                        wdata.etatBoutonNeg = false;
                    break;
            case KEYCODE_BUTTON_HOME :  
                    utility.trace("bt HOME");
                    if(value==0)
                    {
                        if(!activateAccData)
                        {
                            wdata.etatBoutonHome = true;
                            activateorNotAcc(true);
                            activateAccData = true;
                        }else{
                            wdata.etatBoutonHome = false;
                            activateorNotAcc(false);
                            activateAccData = false;
                        }
                    }
                    break;
            case KEYCODE_BUTTON_1 :  
                    utility.trace("bt 1");
                    if(value==0)
                        wdata.etatBouton1 = true;
                    else
                        wdata.etatBouton1 = false;
                    break;
            case KEYCODE_BUTTON_2 :  
                    utility.trace("bt 2");
                    if(value==0)
                        wdata.etatBouton2 = true;
                    else
                        wdata.etatBouton2 = false;
                    break;
            case KEYCODE_BUTTON_HAUT :  
                    utility.trace("bt haut");
                    if(value==0)
                        wdata.etatBoutonHaut = true;
                    else
                        wdata.etatBoutonHaut = false;
                    break;
            case KEYCODE_BUTTON_BAS :  
                    utility.trace("bt bas");
                    if(value==0)
                        wdata.etatBoutonBas = true;
                    else
                        wdata.etatBoutonBas = false;
                    break;
             case KEYCODE_BUTTON_GAUCHE :  
                    utility.trace("bt gauche");
                    if(value==0)
                        wdata.etatBoutonGauche = true;
                    else
                        wdata.etatBoutonGauche = false;
                    break;
             case KEYCODE_BUTTON_DROITE :  
                    utility.trace("bt droite");
                    if(value==0)
                        wdata.etatBoutonDroit = true;
                    else
                        wdata.etatBoutonDroit = false;
                    break;
            default:break;
        }
        return wdata;
       
    }
     private BroadcastReceiver stateCallback = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == null)
				return;
			utility.trace("stateCallback:" + intent.getAction());
			//Filter everything that is not related to this session
			if (!SESSION_NAME.equals(intent.getStringExtra(SESSION_ID)))
				return;
			
			if (intent.getAction().equals(EVENT_REPORT_CONFIG)) {
				utility.trace("Bluez-IME version " + intent.getIntExtra(EVENT_REPORT_CONFIG_VERSION, 0));				
			} else if (intent.getAction().equals(EVENT_REPORTSTATE)) {
				m_connected = intent.getBooleanExtra(EVENT_REPORTSTATE_CONNECTED, false);
				utility.trace("connected:" + m_connected);
				//After we connect, we rumble the device for a second if it is supported
				if (m_connected) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Intent req = new Intent(REQUEST_FEATURECHANGE);
							req.putExtra(REQUEST_FEATURECHANGE_LEDID, 2);
							req.putExtra(REQUEST_FEATURECHANGE_RUMBLE, true);
							activity.startService(req);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
							req.putExtra(REQUEST_FEATURECHANGE_LEDID, 1);
							req.putExtra(REQUEST_FEATURECHANGE_RUMBLE, false);
							activity.startService(req);
						}
					});
				}
				
			} else if (intent.getAction().equals(EVENT_CONNECTED)) {
				m_connected = true;
			} else if (intent.getAction().equals(EVENT_DISCONNECTED)) {
				m_connected = false;
			} else if (intent.getAction().equals(EVENT_ERROR)) {
				utility.trace("Error: " + intent.getStringExtra(EVENT_ERROR_SHORT));
				m_connected = false;
			}
					
		}
	};
	
	private BroadcastReceiver statusMonitor = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == null)
				return;
                        utility.trace("statusMonitor:" + intent.getAction());
			if (!SESSION_NAME.equals(intent.getStringExtra(SESSION_ID)))
				return;
                        if (intent.getAction().equals(EVENT_ACCELEROMETERCHANGE)) {
                            int axis = intent.getIntExtra(EVENT_ACCELEROMETERCHANGE_AXIS, 0);
                            int value = intent.getIntExtra(EVENT_ACCELEROMETERCHANGE_VALUE, 0);
                            //utility.trace("axe:" + axis + " value:" + value);
                            //X:0 Y:1 Z:2
                            if(axis == 0)
                            {
                                xReceived = true;
                                xValue = value;
                            }else if(axis==1 && xReceived)//on a x ety 
                            {
                               exeProg.onReceivedEvent(onReceivedAccData(xValue,value));
                            }
                            
                             
                            
                        }
                        else if (intent.getAction().equals(EVENT_DIRECTIONALCHANGE)) {
				int value = intent.getIntExtra(EVENT_DIRECTIONALCHANGE_VALUE, 0);
				int direction = intent.getIntExtra(EVENT_DIRECTIONALCHANGE_DIRECTION, 100);
                                utility.trace("key:" + value + " action:" + direction);
				
                               
                                /*SeekBar sbar = null;
				switch (direction) {
					case 0:
						sbar = m_axisX1;
						break;
					case 1:
						sbar = m_axisY1;
						break;
					case 2:
						sbar = m_axisX2;
						break;
					case 3:
						sbar = m_axisY2;
						break;
				}
				
				if (sbar != null) {
					sbar.setProgress(Math.min(Math.max(0, 128 + value), sbar.getMax()));
				}
				else {
					reportUnmatched(String.format(getString(R.string.unmatched_axis_event), direction + "", value + ""));
				}*/
				
				
			} else if (intent.getAction().equals(EVENT_KEYPRESS)) {
				int key = intent.getIntExtra(EVENT_KEYPRESS_KEY, 0);
				int action = intent.getIntExtra(EVENT_KEYPRESS_ACTION, 100);
				utility.trace("key:" + key + " action:" + action);
                                
				exeProg.onReceivedEvent(onReceivedBtCommand(key,action));
			}
		}
	};
    
}
