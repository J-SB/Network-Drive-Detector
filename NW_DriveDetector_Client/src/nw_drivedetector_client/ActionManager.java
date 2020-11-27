package nw_drivedetector_client;
import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Date;

class ActionManager 
{
    int response_action;
    LinkedList<String> activeDrives;
    
    ActionManager()
    {
        //Delegation of Constructor call.
        this("f:/rahulcomp/temp/actions.txt");
                
        
        //FYI 1:
        //* this is used in a constructor to delegate the
        //call to another constructor of the same class.
        //* super is used in a constructor to delegate the
        //call to a constructor of the super (parent) class.
        

        //FYI 2: 
        //*this and super are used to share data (parameters)
        //across the constructors.
        
        //FYI 3: 
        //* Call to "this" must be the first statement
        //in the constructor.
        //* Call to "super" must be the first statement
        //in the constructor.
        //* Hence super and this cannot be used together.
        
    }
        
    ActionManager(String path) 
    {
        activeDrives = new LinkedList<String>();
        
        //try fetching action and report from the file
        try
        {
            //open the permission file for reading
            FileReader fr = new FileReader(path);
            //Apply a BufferedReader to the Filereader
            BufferedReader br = new BufferedReader(fr);
            String s;
            String arr[] = new String[2];
        
            int i =0;
            while( i < 2 && (s = br.readLine()) != null)
            {
                arr[i++]= s;
            }
            //close the file
            fr.close();
            
            //actions are defined
            String temp_arr[];
            //tokenize the strings
            temp_arr = arr[0].split(":");//[0] = "Action", [1] = flagvalue
            this.response_action = Integer.parseInt(temp_arr[1]);

        }
        catch(Exception ex)
        {
            //apply default permissions
            this.response_action =  Flags.ACTION_STRICT;
        }
        
    }
    
    void addToActiveDrives(LinkedList<String> pluggedIn)
    {
        for(String pI : pluggedIn)
        {
            if(!activeDrives.contains(pI))
                activeDrives.add(pI);
        }
    }
    
    void removeFromActiveDrives(LinkedList<String> pluggedOut)
    {
        for(String pO : pluggedOut)
        {
            System.out.println(pO + " : " + activeDrives.remove(pO));
        }
        pluggedOut.clear();
    }
    
    void sendReport(String report)
    {
        try
        {
            File f = new File(report);
            if(f.exists())
            {
                new ReportSender("127.1.1.1", 8900,f);
            }
            else
            {
                System.out.println("Recording Not Accessible");
            }
        }
        catch(ConnectException cex)
        {
            System.out.println("Server Offline");
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
    
    void actNow(LinkedList<String> pluggedIn)
    {
        addToActiveDrives(pluggedIn);
        new Actor(1);
    }
    
    void stopAction(LinkedList<String> pluggedOut)
    {
        removeFromActiveDrives(pluggedOut);
        new Actor(2);
    }
    
    class Actor extends Thread
    {
        int actionFlag;
        Actor(int flag)
        {
            this.actionFlag = flag;
            start();
        }
        
        public void run()
        {
            if(actionFlag == 1)
                process();
            else if(actionFlag == 2)
                stopProcess();
        }
     
        void stopProcess()
        {
            if(activeDrives.size() == 0)
            {
                TTS.getSynthesizer().stopspeaking();
                try
                {
                    String report = ScreenManager.getScreenManager().stopRecording();
                    sendReport(report);
                }
                catch(Exception ex)
                {
                    System.out.println("Screen Recording Object : Undefined");
                }
                
                try
                {
                    InputMessup.getMessup().stopMessup();
                }
                catch(Exception ex)
                {
                    System.out.println("Screen Recording Object : Undefined");
                }
            }
        }
        
        void process()
        {
            //DEFAULT + LENIENT ACTION
            //TTS and ScreenRecord
            String text = "";
            for(String temp : activeDrives)
            {
                temp = temp.substring(0,temp.length()-1);
                text = text + "U.S.B. drive, "+ temp + " detected!!!";
            }
            tts(text);

            recordScreen();
            
            if(response_action == Flags.ACTION_INTERMEDIATE)
            {
                inputMessup();
            }
            else if(response_action == Flags.ACTION_STRICT)
            {
                unmountDrives();
            }
        }
             
        void inputMessup()
        {
            try
            {
                InputMessup.getMessup().messup();
            }
            catch(Exception ex)
            {}
        }
        
        void tts(String text)
        {
            TTS.getSynthesizer().speak(text);
        }
        
        void recordScreen()
        {
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_H_m_s");
                String fileName = System.getProperty("user.name") + "_" + InetAddress.getLocalHost().toString().replace('/', '_').replace('.', '_') + sdf.format(new Date()) + ".avi";
                ScreenManager.getScreenManager().screenRecord("f:/rahulcomp/temp/" + fileName);
            }
            catch(Exception ex)
            {}
        }
        
        boolean unmountDrives()
        {
            try
            {
                //Access javas runtime object
                Runtime r = Runtime.getRuntime();
                //know the OS
                String os = System.getProperty("os.name");

                Process p = null;
                if(os.toUpperCase().startsWith("WINDOWS"))
                {//Windows specific command
                    //Use the exec() fire the shell command you wish
                    for(String drive: activeDrives)
                    {
                        p= r.exec("mountvol " + drive + " /P");
                        //Let the command complete
                        p.waitFor();
                    }
                }
            }
            catch(Exception ex)
            {
                System.out.println("Err in unmount");
            }
            return activeDrives.size() == 0;
        }
    }
    
}