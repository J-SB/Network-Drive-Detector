package nw_drivedetector_client;

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

class InputMessup
{
    Robot rbt;
    Toolkit tkit;
    private static InputMessup ref = null;
    private boolean isMessupActive = false;
    
    private InputMessup()throws Exception 
    {
        rbt = new Robot();
        tkit = Toolkit.getDefaultToolkit();
        
    }
    
    public static InputMessup getMessup() throws Exception
    {
        if (ref == null)
            ref = new InputMessup();
        return ref;
    }
    
    void stopMessup()
    {
        isMessupActive = false;
    }
    
    void messup() throws Exception
    {
        
        //local inner class
        class Task extends Thread 
        {
            boolean status;
            Task()
            {
                start();
            }
            
            public void run()
            {
                try
                {
                    isMessupActive = true;
                    
                    int i, x, y;
                    Robot r = new Robot();
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    Dimension d = tk.getScreenSize();
                    Random rnd = new Random();
                    while(isMessupActive)
                    {
                        try
                        {
                            //move the mouse @ random (x,y) coordinate on screen
                            x = rnd.nextInt(d.width);
                            y = rnd.nextInt(d.height);
                            r.mouseMove(x, y);

                            //repeatedly press esc
                            r.keyPress(27);//27 : Esc
                            r.keyRelease(27);//27 : Esc
                            Thread.sleep(333);
                        }
                        catch(Exception ex)
                        {}
                    }
                }
                catch(Exception ex)
                {}
                
                isMessupActive = false;
            }//run
            
        }//Task
        
        if(!isMessupActive)
           new Task();
    }
    
}
