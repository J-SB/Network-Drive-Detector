package nw_drivedetector_server;

public class Main {

    public static void main(String[] args) 
    {
        try
        {
            new Server(8900);
        }
        catch(Exception ex)
        {
            System.out.println("Err: " + ex);
        }
        
    }//main
    
}
