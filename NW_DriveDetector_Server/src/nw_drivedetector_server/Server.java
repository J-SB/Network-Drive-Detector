package nw_drivedetector_server;

import java.io.*;
import java.net.*;

public class Server 
{
    ServerSocket port;
    ConnectionManager cmgr;
    
    Server(int portNumber) throws IOException
    {
        //open a port
        port = new ServerSocket(portNumber);
        //activate the ConnectionManager
        cmgr = new ConnectionManager();
        
    }
    
    //nested class
    class ConnectionManager extends Thread
    {
        boolean connection_flag;
        
        ConnectionManager()
        {
            connection_flag = true;
            start();//activate the thread
        }
        
        void stopConnectionManager()
        {
            connection_flag = false;
        }
        
        //on start: this code is executed concurrently by the ConnectionManager thread
        public void run()
        {
            while(connection_flag )
            {
                acceptConnection();
            }
            try
            {
                port.close();
            }
            catch(Exception ex)
            {}
        }//run

        void acceptConnection()
        {
            
            //port.accept()
            //1) Blocks the program control and waits for a client connection request. 
            //2) On a client connection request.
            //3) Forms a connection (stream of data transfer between the server and the client).

            try
            {
                System.out.println("Waiting for a client connection request...");
                Socket s = port.accept();
                
                //a new thread per connection i/o
                new ProcessConnection(s);
                System.out.println("... Got a client connection request!!!");
            }
            catch(Exception ex)
            {}
        }
    }
    
    //ProcessConnection
    class ProcessConnection extends Thread
    {
        Socket client;
        ProcessConnection(Socket s)
        {
            client = s;
            start();//activate the thread
        }
        
        //per connection process
        public void run()
        {
            try
            {
                //socket streams 
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();
                
                //convenience class so that streams are used for
                //i/o of datatypes rather than bytes only.
                DataInputStream din = new DataInputStream(in);
                DataOutputStream dout = new DataOutputStream(out);
                
                //i/o
                String fname = "f:/rahulcomp/temp1/" + din.readUTF();//client has sent
                long fsize = din.readLong();
                long cnt = 0;
                int n;
                byte arr[] = new byte[512];
                FileOutputStream fout = new FileOutputStream(fname);
                while(cnt < fsize)
                {
                    n = din.read(arr);
                    cnt = cnt+n;
                    fout.write(arr, 0, n);
                }
                fout.flush();
                fout.close();
                //close the connection
                client.close();
            }
            catch(Exception ex)
            {
                System.out.println("Err: " + ex);
            }
        }
    }
}
