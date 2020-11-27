package nw_drivedetector_client;

//Speech Synthesis
import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.LinkedList;
class TTS 
{
    private static TTS objReference = null;
    private Synthesizer speech_synth;
    private LinkedList <String> text_queue;
    private Speaker speaker;
    private boolean keep_speaking ;
    
    private TTS() throws Exception //limited to use by current class only
    {
        text_queue = new LinkedList<String>();
        keep_speaking = true;
        speaker = new Speaker();
        //A) Speech Engine (resource) Allocation
        //* Identify the available Speech Engines
        EngineList allEngines = Central.availableSynthesizers(null); //Get a list of all available Speech Synthesizers. Parameter null means no filters.
        //* Of the available Speech Engines, select and instantiate (allocate) one.
        speech_synth = Central.createSynthesizer((EngineModeDesc) allEngines.elementAt(1));
        
        
        //B)H/W (sound card, etc) resource ownership
        speech_synth.allocate();
        
        //C) Switch the Speech Engine (FSM) from default paused to resumed state, due which audio streaming becomes possible  
        speech_synth.resume();
        
    }//TTS()
    
    
    void speak(String text)
    {
        text_queue.add(text);
    }
    
    void stopspeaking()
    {
        text_queue.clear();
    }
    
    class Speaker extends Thread
    {
        Speaker()
        {
            start();
        }
        
        public void run()
        {
            while(keep_speaking)
            {
                try
                {
                    announce();
                    Thread.sleep(1000);
                }
                catch(Exception ex)
                {}
            }
        }
        
        void announce()
        {
            try
            {
                String text;
                while(text_queue.size() > 0)
                {
                    text = text_queue.remove(0);
                    //speak this text
                    speech_synth.speakPlainText(text, null);
                    //wait until speaking is over
                    speech_synth.waitEngineState(speech_synth.QUEUE_EMPTY);
                }
            }
            catch(Exception ex)
            {}

        }//announce
    

    
    }
    void deallocate()
    {
        try
        {
            speech_synth.deallocate();
        }
        catch(Exception ex){}
    }
    
    //factory method
    public static TTS getSynthesizer()
    {
        try
        {
            if(objReference == null)//is the reference initialized?
            {
                objReference = new TTS(); //initialize 
            }
        }
        catch(Exception ex){}
        return objReference;
    }
    
}

