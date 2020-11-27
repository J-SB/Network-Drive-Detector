package nw_drivedetector_client;

import java.awt.*;
import java.awt.image.*;
import org.opencv.core.*;
import org.opencv.videoio.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

class ScreenManager 
{
    Robot rbt;
    Toolkit tkit;
    Rectangle rect;
    private static ScreenManager ref = null;
    private boolean isRecordingActive = false;
    private String currentFileName = null;
    
    private ScreenManager()throws Exception 
    {
        rbt = new Robot();
        tkit = Toolkit.getDefaultToolkit();
        rect = new Rectangle(tkit.getScreenSize());
        
        //load the open cv libraries and codec
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_videoio_ffmpeg440_64");
        
    }
    
    public static ScreenManager getScreenManager() throws Exception
    {
        if (ref == null)
            ref = new ScreenManager();
        return ref;
    }
    
    String stopRecording()
    {
        isRecordingActive = false;
        String temp = currentFileName;
        currentFileName = null;
        return temp;
    }
    
    
    void screenRecord(String fileName) throws Exception
    {
        if(isRecordingActive)
            return;
                    
        currentFileName = fileName;
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
                    isRecordingActive = true;
                    //1) Open a VideoWriter
                    Size frameSize = new Size(rect.width, rect.height);
                    int fourCC = VideoWriter.fourcc('X','V', 'I', 'D');
                    double fps = 25;
                    VideoWriter vw = new VideoWriter(fileName, fourCC, fps, frameSize);

                    int x,y;
                    byte arr[] = new byte[3];
                    BufferedImage bi;
                    Raster rst;
                    String un_comp_ip = "User: " + System.getProperty("user.name") + "/" + InetAddress.getLocalHost();
                    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:m:s");
                    Graphics g;
                    Font fnt = new Font("Arial", Font.BOLD, 20);
                    Date d = new Date();
                    
                    //3) Record the screen
                    while(isRecordingActive)
                    {
                        //3.1)Have a screen grab
                        bi = rbt.createScreenCapture(rect);
                        
                        //3.2)External water mark
                        g = bi.getGraphics();
                        g.setColor(Color.RED);
                        g.setFont(fnt);
                        g.drawString(un_comp_ip, 950, 650);
                        d.setTime(System.currentTimeMillis());
                        g.drawString(sdf.format(d), 950, 700);
                        
                        //3.3)Convert the BufferedImage into a Mat (ndarray)
                        rst = bi.getData(); //buffered image converted into a matrix of pixels
                        
                        Mat mat = new Mat(frameSize, CvType.CV_8UC3);                        
                        
                        for(x =0; x < rect.width; x++)//width
                        {
                            for(y =0; y < rect.height; y++)//height
                            {
                                arr[2] = (byte) rst.getSample(x, y, 0);//r
                                arr[1] = (byte) rst.getSample(x, y, 1);//g
                                arr[0] = (byte) rst.getSample(x, y, 2);//b

                                mat.put(y, x, arr);//orientation is height, width
                            }
                        }
                        //3.4) VideoWrite
                        vw.write(mat);
                        vw.write(mat);
                        vw.write(mat);
                        vw.write(mat);
                    }
                    vw.release();
                }
                catch(Exception ex)
                {
                    System.out.println("Err");
                }
                isRecordingActive = false;
            }//run
            
        }//Task
        
        if(!isRecordingActive)
           new Task();
    }
    
    
}
