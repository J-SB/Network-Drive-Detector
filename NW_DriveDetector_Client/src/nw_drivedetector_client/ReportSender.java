package nw_drivedetector_client;

import java.net.*;
import java.io.*;

//Connect to the server and send client side activity report
public class ReportSender extends Thread
{
    Socket svrConnection;
    File report;
    public ReportSender(String serverIp, int serverPort, File report) throws Exception 
    {
        //lets connect to the server (requesting a connection)
        svrConnection = new Socket(serverIp, serverPort);
        this.report = report;
        start();
    }
    
    public void run()
    {
        try
        {
            //send report
            //sockets input stream
            InputStream in = svrConnection.getInputStream();
            DataInputStream din  = new DataInputStream(in);
            //sockets output stream
            OutputStream out = svrConnection.getOutputStream();
            DataOutputStream dout  = new DataOutputStream(out);

            //send the report
        
            dout.writeUTF(report.getName());
            long fsize = report.length();
            long cnt=0;
            dout.writeLong(fsize);
        
            FileInputStream fin = new FileInputStream(report);
            byte arr[] = new byte[512];
            int n;
            
            while(cnt < fsize)
            {
                n = fin.read(arr);
                dout.write(arr, 0, n);
                cnt = cnt + n;
            }
            fin.close();
            svrConnection.close();
            
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        
    }
    
}
