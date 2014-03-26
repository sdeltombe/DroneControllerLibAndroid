/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlib.pkg;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
/**
 *
 * @author Seb
 */
public class SocketController extends Controller implements Runnable  {

    private int port = 12345;
    private ServerSocket socket;
    static final int DELAY_IN_MS = 100;
    private boolean listen = true;
    float ancienneValeurY, ancienneValeurX;
    WiimoteData wiimoteData = new WiimoteData();
    WiimoteCommander exeProg = new WiimoteCommander(this);
    Socket client;
    public SocketController(Utility utility, Object[] args) {
        super(utility,args);
        if(args!=null && args.length > 0 )
        {
            port = (Integer)args[0];
        }
    }
    @Override
    public void connect() {
        try{
            socket = new ServerSocket(port);
            utility.trace("SocketContoller is running on port :" + port);
        }catch(IOException ex)
        {
            utility.traceError("Error during connect", ex);
        }
        
    }

    @Override
    public void listen() {
       new Thread(this).start();
    }

    @Override
    public void disconnect() {
        closeClient();
        try{
            if(socket!=null)
                socket.close();
            listen = false;
        }catch(IOException ex)
        {
            utility.traceError("Error during connect", ex);
        }
    }
    
    private void closeClient()
    {
        try{
            if(client!=null)
            {
                    client.close();
                    client = null;
            }
        }catch(IOException ex)
        {
            utility.traceError("Error during connect", ex);
        }
        
    }
    
    private void waitClient()
    {
        
        while(client == null)
        {
                try{
                    // Accept new TCP client
                    client       = socket.accept();
                    //client.setSoTimeout(60000);
                    System.out.println("New client, address " + client.getInetAddress() + " on " + client.getPort() + ".");
                    utility.threadSleep(DELAY_IN_MS);
                }catch(IOException ex)
                {
                    utility.traceError("Error during waitClient", ex);
                }
        }
       
    }

    @Override
    public void run() {
        
        waitClient();
        while(listen)
        {
                try{
                    InputStream input = client.getInputStream();
                        
                    String response = new BufferedReader(new InputStreamReader(input)).readLine();
                    if(response!=null)
                    {
                        //utility.trace("Received message requestId:" + response);
                        sortCommand (response);
                        exeProg.onReceivedEvent(wiimoteData);
                    }
                }
                catch(SocketTimeoutException ex)
                {
                    utility.traceError("Timeout during run", ex);
                    closeClient();
                    waitClient();
                }
                catch(IOException ex)
                {
                    utility.traceError("Error during run", ex);
                    closeClient();
                    waitClient();
                }
                
        }
    }
    @SuppressWarnings("empty-statement")
    private void sortCommand(String command)
    {
        String button;
        button = command.substring(1, command.length());
        if(command.startsWith("p"))//Bouton pressé
        {
            setCommand (button, true);
        }
        else if (command.startsWith("r"))//Bouton relaché
        {
            setCommand (button, false);
        }
        else if ((command.startsWith("y") && Float.parseFloat(command.substring(1, command.length())) != ancienneValeurY)
                    || command.startsWith("x") && Float.parseFloat(command.substring(1, command.length())) != ancienneValeurX);
        {
            if (command.startsWith("y"))
            {
                wiimoteData.angleRoll = Float.parseFloat(button);
                ancienneValeurY = Float.parseFloat(command.substring(1, command.length()));
            }
            else if (command.startsWith("x"))
            {
                wiimoteData.anglePitch = Float.parseFloat(button);
                ancienneValeurX = Float.parseFloat(command.substring(1, command.length()));
            }
        }
        
        
    }
    
    private void setCommand (String button, boolean a)
    {
        if (button.equals("A"))
        {
            wiimoteData.etatBoutonA = a;
        }
        else if (button.equals("B"))
        {
            wiimoteData.etatBoutonB = a;
        }
        else if (button.equals("ONE"))
        {
            wiimoteData.etatBouton1 = a;
        } 
        else if (button.equals("TWO"))
        {
            wiimoteData.etatBouton2 = a;
        }
        else if (button.equals("UP"))
        {
            wiimoteData.etatBoutonHaut = a;
        } 
        else if (button.equals("DOWN"))
        {
            wiimoteData.etatBoutonBas = a;
        }
        else if (button.equals("LEFT"))
        {
            wiimoteData.etatBoutonGauche = a;
        }
        else if (button.equals("RIGHT"))
        {
            wiimoteData.etatBoutonDroit = a;
        }
        else if (button.equals("PLUS"))
        {
            wiimoteData.etatBoutonPos = a;
        }
        else if (button.equals("MINUS"))
        {
            wiimoteData.etatBoutonNeg = a;
        }
        else if (button.equals("HOME"))
        {
            wiimoteData.etatBoutonHome = a;
        }
    }
    
}
