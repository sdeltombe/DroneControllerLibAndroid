/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlib.pkg;
/* use for getVersion in progress...
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;*/
import java.net.*;
import java.util.*;
/**
 *
 * @author Seb
 */
public class ArDroneCommander /*implements Runnable*/extends Thread {
    InetAddress inet_addr;
    DatagramSocket socket;
    int seq = 1; //Send AT command with sequence number 1 will reset the counter
    
    static final String DEFAULT_IP = "192.168.1.1";
    static final int DEFAULT_PORT = 5556;
    static final int FTP_PORT = 5551;
    static final int DELAY_IN_MS = 33;
    private Utility utility;
    
    private ArDroneCommand cmd;
    private ArDroneReceiver receiver;
    private Boolean send = true;
    private Boolean isLanding=false;
    private Boolean isMoving=false;
 
    NavData navData = new NavData();
    
    /** Holder */
    private static class SingletonHolder
    {	
    /** Instance unique non préinitialisée */
        private final static ArDroneCommander instance = new ArDroneCommander();
    }

    /** Point d'accès pour l'instance unique du singleton
     * @return  */
    public static ArDroneCommander getInstance()
    {
        return SingletonHolder.instance;
    }
    
    private ArDroneCommander()
    {
        
    }
   
    
    public void init(Utility utility)
    {
        this.utility = utility;
       
        StringTokenizer st = new StringTokenizer(DEFAULT_IP, ".");

	byte[] ip_bytes = new byte[4];
	if (st.countTokens() == 4){
 	    for (int i = 0; i < 4; i++){
		ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
	    }
	}
	else {
	    utility.trace("Incorrect IP address format: " + DEFAULT_IP);
	    System.exit(-1);
	}
	
	utility.trace("IP: " + DEFAULT_IP); 	

        try{
            inet_addr = InetAddress.getByAddress(ip_bytes);
            socket = new DatagramSocket();
        }
        catch(SocketException ex)
        {
            utility.traceError("SocketException during send AT*CONFIG:", ex);
            
        }
        catch(UnknownHostException ex)
        {
            utility.traceError("UnknownHostException during send AT*CONFIG:", ex);
            
        }        
    }
    
    
    public void init()
    {
        //undocumented command
        send_at_cmd("AT*PMODE=" + (seq++) + ",2");
        send_at_cmd("AT*MISC=" + (seq++) + ",2,20,2000,3000");
        
        //pour la calibration
        sendFlatTrim();
        
                
        send_at_cmd("AT*CONFIG=" + (seq++) + ",\"control:altitude_max\",\"3000\""); //altitude max 1.5m
        send_at_cmd("AT*CONFIG=" + (seq++) + ",\"control:outdoor\",\"FALSE\"");
 
        receiver = new ArDroneReceiver(navData,inet_addr,utility);
        //on lance le thread du receiver
        receiver.start();
    }
    
    @Override
    public void run() {
        init();
        int watchDogCounter = 0;
        while(send)
        {
            if(navData.getState(NavData.FLYING) && !isLanding)
            {
                if(!isMoving)
                {
                    utility.trace("===hovering===");
                    hovering();
                }else{
                    send_at_cmd("AT*PCMD=" + (seq++) + ",1," + 
                            cmd.getLeftRightTilt() + "," + //tourner à gauche/tourner à droite
                            cmd.getFrontBackTilt() + "," + //avant/arriere
                            cmd.getVerticalSpeed() + "," + //haut/bas
                            cmd.getAngularSpeed()); //rotation gauche/droite
                    
                }
            }
            utility.threadSleep(DELAY_IN_MS);
            watchDogCounter++;
            if(watchDogCounter==3)//100ms a peu pres
            {
                watchDogCounter = 0;
                this.SendResetWatchDog();
            }
        }
    }
    
    public void sendCommand(ArDroneCommand cmd)
    {
        this.cmd = cmd;
        switch(cmd.action)
        {
            case TAKE_OFF: takeOff(); break;
            case LANDING : landing(); break;
            case HOVERING: hovering(); break;
            case MOVING : isMoving = true; break;    
            default:break;
        }
    }
     
    public void send_at_cmd(String at_cmd) {
    	utility.trace("AT command: " + at_cmd);    	
	byte[] buffer = (at_cmd + "\r").getBytes();
	DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, DEFAULT_PORT);
	
        try{
            socket.send(packet);
            utility.threadSleep(DELAY_IN_MS);
        }catch (java.io.IOException ex)
        {
            utility.traceError("IOException during send_at_cmd:", ex);
        }catch(Exception ex)
        {
            utility.traceError("Exception during send_at_cmd:", ex);
        }
      	
    }

    public void SendStopBOOTSTRAP()
    {
          //specify now to exit of the BOOTSTRAP mode
          utility.trace("=>SendStopBOOTSTRAP to DEFAULT_PORT");
          send_at_cmd("AT*CONFIG=" + (seq++) + ",\"general:navdata_demo\",\"FALSE\"");
          //send_at_cmd("AT*CONFIG=" + (seq++) + ",\"general:navdata_demo\",\"TRUE\"");
          utility.threadSleep(DELAY_IN_MS);
          send_at_cmd("AT*CTRL=" + (seq++) + ",0");
          
        
    }
    
    public void SendResetWatchDog()
    {
        send_at_cmd("AT*COMWDG="+ (seq++));
    }
    
    
    public void hovering()
    {
        isMoving = false;
      
        send_at_cmd("AT*PCMD=" + (seq++) + ",0,0,0,0,0");
    }
    
    public void landing()
    {
        isLanding = true;
        isMoving = false;
        send_at_cmd("AT*REF=" + (seq++) + ",290717696");
    }
    //a appeler avant le decollage pour indiquer au drone qu'il est 
    //su un plan horizontal de reference
    public void sendFlatTrim()
    {
        send_at_cmd("AT*FTRIM=" + (seq++) + ",");
    }
    
     public void takeOff()
    {
        int i=0;
        isLanding = false;
        isMoving = false;
        utility.trace("TakeOff !! ds ArDroneCommander");
        
        while(!navData.getState(NavData.FLYING))
        {
            send_at_cmd("AT*REF=" + (seq++) + ",290718208");
            i++;
            if(i>200)
            {
               utility.trace(" takeOff timeout");
               break;
            }
        }
        
        
    }
     /* EN TRAVAUX pas nécessaire
    public String getVersion()
    {
        String response = new String();
         
        try{
           
            Socket socketTcp = new Socket(DEFAULT_IP,FTP_PORT);
            InputStream input = socketTcp.getInputStream();
            OutputStream ouput = socketTcp.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ouput));
            
            //Reception du Message de Bienvenue on ne fait rien avec...
            response = reader.readLine();
            if(!response.startsWith("220 "))
            {
                utility.trace( "unknown response when connecting to the FTP server: " + response);
                 socketTcp.close();
                return "Error during getVersion";
            }
            
            //Log en anonymous
            writer.write("USER anonymous\r\n");
            writer.flush();
            response = reader.readLine();
            if(!response.startsWith("230 "))
            {
                utility.trace( "unknown response after sending the user: " + response);
                 socketTcp.close();
                return "Error during getVersion";
            }
                        
            //Set to PASV mode
            writer.write("PASV\r\n");
            writer.flush();                       
            response = reader.readLine();
            if (!response.startsWith("227 ")) 
            {
                    utility.trace( "unknown response after sending passive mode: " + response);
                     socketTcp.close();
                    return "Error during getVersion";
            }
            
            int port = -1;
            int opening = response.indexOf('(');
            int closing = response.indexOf(')', opening + 1);
            if (closing > 0) {
              String dataLink = response.substring(opening + 1, closing);
              StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
              try {
                    tokenizer.nextToken();
                    tokenizer.nextToken();
                    tokenizer.nextToken();
                    tokenizer.nextToken();
                    int a = Integer.parseInt(tokenizer.nextToken());
                    int b = Integer.parseInt(tokenizer.nextToken());
                    port = a * 256 + b;
              } catch (NumberFormatException e) {
                utility.traceError("getVerion()", e);
              }
            }
            socketTcp.close();
            
            socketTcp = new Socket(DEFAULT_IP,port);
           
            input = socketTcp.getInputStream();
            ouput = socketTcp.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            writer = new BufferedWriter(new OutputStreamWriter(ouput));
           
            
            writer.write("RETR version.txt\r\n");
            writer.flush();
            response = reader.readLine();
            
            socketTcp.close();
            
            
        }catch (UnknownHostException ex)
        {
            utility.traceError("getVersion()", ex);
        }
        catch (IOException ex)
        {
            utility.traceError("getVersion()", ex);
        }
        return "Version du Firmware :" + response;
    }
    */
   
    
    
}
