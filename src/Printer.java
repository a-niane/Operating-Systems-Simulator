import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
public class Printer 
{
    private final File the_file;
    public Printer(int id) 
    {
        the_file = new File("PRINTER" + id);
    }

    public void print(StringBuffer content) 
    {
        try 
        {
            if (!the_file.exists()) 
            {
                the_file.createNewFile();
            }
            FileWriter reader = new FileWriter(the_file, true);    
            BufferedWriter writer = new BufferedWriter(reader);
            try  
            {
                writer.write(content.toString());
                writer.newLine();
                writer.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }        
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}