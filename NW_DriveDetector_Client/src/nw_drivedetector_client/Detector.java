package nw_drivedetector_client;

import java.io.File;
import java.util.LinkedList;

public class Detector extends Thread
{
    boolean detection_flag;
    LinkedList <String> originalDrives;
    LinkedList<String> pluggedInDrives;
    LinkedList<String> pluggedOutDrives;
    ActionManager aMgr;
    
    Detector()
    {
        detection_flag = true;
        pluggedInDrives = new LinkedList<String>();
        pluggedOutDrives = new LinkedList<String>();
        originalDrives = new LinkedList<String>();
        originalDrives.add("C:\\");
        originalDrives.add("D:\\");
        originalDrives.add("E:\\");
        originalDrives.add("F:\\");
        originalDrives.add("G:\\");
        originalDrives.add("H:\\");
        
              
        aMgr = new ActionManager();
        start();//activate the thread
    }
    
    //method to execute concurrently (by the Detector thread)
    public void run()
    {
        while(detection_flag)//polling
        {
            try
            {
                track();
                Thread.sleep(4000);//delay
            }
            catch(Exception ex)
            {}
        }//while
    }
    
    void stopPolling()
    {
        detection_flag = false;
    }
    
    void track()throws Exception
    {
        //Detecting the currently available drives
        File allDrives[] = File.listRoots();
        //Runtime rt = Runtime.getRuntime();
        for(File currentDrive : allDrives)
        {
            //System.out.println("* " + currentDrive.getAbsolutePath());
            if(!originalDrives.contains(currentDrive.getAbsolutePath()))
                if(!pluggedInDrives.contains(currentDrive.getAbsolutePath()))
                    pluggedInDrives.add(currentDrive.getAbsolutePath());
        }

        //look for plugged in drives that are not found in current drives list
        int i;
        boolean flag;
        for(String pluggedIn : pluggedInDrives)
        {
            flag = false;
            for(i =0; i < allDrives.length; i++)
            {
                if(pluggedIn.equals(allDrives[i].getAbsolutePath()))
                {
                    flag = true;//found (still plugged in)
                    break;
                }
            }
            if(flag== false)
            {
                //pluggedIn is not found (now plugged out)
                pluggedOutDrives.add(pluggedIn);
                pluggedInDrives.remove(pluggedIn);
            }
        }//for
        
        //now pluggedIn list is refreshed to reflect current plugged in drives
        if(pluggedOutDrives.size() >0)
        {
            System.out.println("PLUGGED OUT");
            aMgr.stopAction(pluggedOutDrives);
        }
        
        if(pluggedInDrives.size() > 0)
        {//one to many new drives attached
            aMgr.actNow(pluggedInDrives);
        }
        
           
        System.out.println("-----------------");
    }
    
}
