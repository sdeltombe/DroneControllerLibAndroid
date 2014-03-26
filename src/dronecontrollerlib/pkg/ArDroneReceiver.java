
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dronecontrollerlib.pkg;


import static dronecontrollerlib.pkg.ArDroneCommander.DELAY_IN_MS;
import static dronecontrollerlib.pkg.NavData.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 *
 * @author Seb
 */
public class ArDroneReceiver extends Thread {

    private DatagramSocket socket;
    private final Utility utility;
    private boolean listen=true;
    static final int NAVDATA_PORT = 5554;
    static final int DELAY_IN_MS = 30;
    
    static final int NAV_STATE_OFFSET = 4;
    
    final InetAddress inet_addr;
    ArDroneCommander commander;
    NavData navData;
    
    public ArDroneReceiver(NavData navData,InetAddress inet_addr,Utility utility)
    {
        this.inet_addr = inet_addr;
        this.utility = utility;
        this.commander = ArDroneCommander.getInstance();
        this.navData = navData;
        try{
            this.socket = new DatagramSocket();
            socket.connect(inet_addr, NAVDATA_PORT);
            //configuration du timeout de reception
            this.socket.setSoTimeout(3000);
        }catch(SocketException ex)
        {
            utility.traceError("ArDroneReceiver SocketException:", ex);
        }
        catch(Exception ex)
        {
            utility.traceError("ArDroneReceiver Exception:", ex);
        }
        
    }
    
    
    public void halt()
    {
        listen = false;
    }
    
    
    public void sendInitMessage()
    {
        //indicate to the drone to respond just to us
        byte[] bufInit = {0x01,0x00,0x00,0x00};
        DatagramPacket packetInit = new DatagramPacket(bufInit, bufInit.length);/*, inet_addr, NAVDATA_PORT);*/
        try{
            //utility.trace("=>SendInitMessage to NAVDATA_PORT");
            socket.send(packetInit);
        }catch (java.io.IOException ex)
        {
            System.out.println(ex.getMessage());
        }
         catch(Exception ex)
        {
            utility.traceError("ArDroneReceiver SendInitMessage Exception:", ex);
        }
    }
    
   
       
    
    public void connect()
    {
         try{
            sendInitMessage();

            utility.threadSleep(DELAY_IN_MS);
            commander.SendStopBOOTSTRAP();
            utility.threadSleep(DELAY_IN_MS);
         }catch(Exception ex)
            {
                utility.traceError("ArDroneReceiver connect Exception:", ex);
            }
    }
    private int get_int(byte[] data, int offset)
    {
        int value = 0;
        for(int i=3;i>=0;i--)
        {
            int shift = i * 8;
            value += (data[offset + i] & 0x000000FF) << shift;
        }
        return value;
    }
     public int byteArrayToShort(byte[] b, int offset)
    {
        return ((b[offset + 1] & 0x000000FF) << 8) + (b[offset] & 0x000000FF);
    }
      
    
    @Override
    public void run() {
       
        byte[] buffer = new byte[65507];  
        connect();
        
     
        while(listen)
        {
            try{
                if(socket != null)
                {
                    sendInitMessage();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    
                    //utility.trace("NAVDATA received length :" + packet.getLength() + "\n");
                    int header = get_int(packet.getData(),0);
                    if(header != 0x55667788)
                    {
                         utility.trace("Error Parsing NavData header:" + header);
                    }else
                    {
                         //utility.trace("header ok");
                    }
                    int offset = NAV_STATE_OFFSET;
                    int state = get_int(packet.getData(),offset);
                    offset+=4;
                  
                    navData.parseNavData(state);
                    if(navData.IsUpdate())
                    {
                        utility.trace("" + navData);
                    }else{
                        //utility.trace("no change in navData...sequence:" + sequence);
                    }
                    
                    utility.threadSleep(DELAY_IN_MS);
                }
            }
            catch(java.io.IOException ex)
            {
                if(ex.getMessage()!=null && ex.getMessage().contains("Receive timed out"))
                {
                    connect();
                }
                utility.traceError("IOException during receive message", ex);
            }
            catch(Exception ex)
            {
                 utility.traceError("Exception during receive message", ex);
            
            }
            
        }
    }
    
}