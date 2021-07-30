import java.util.Scanner; 
import java.io.File;  
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
public class UserThread extends Thread 
{
    private int the_id;
    private String fileName;
    private File file;

    int currentDisk = 0;
    private StringBuffer currentLine;
    StringBuffer currentFile = null;
    
    Disk[] disks;
    Printer[] printers;
    DiskManager diskManager;
    ResourceManager otherDisks;
    ResourceManager printerManager;
    ArrayList <String> messageLog;
    public UserThread (int id, DiskManager DM, ResourceManager o, ResourceManager PM, Disk[] d, Printer[] p, ArrayList <String> m)
    {
        the_id = id;
        fileName = "inputs/USER" + id;
        this.file = new File(fileName);
        currentLine = new StringBuffer();
        diskManager = DM;
        otherDisks = o;
        disks = d; 
        printers = p;
        printerManager = PM;
        messageLog = m;
    }

    public void run() 
    {
        try
        {
            FileReader readable = new FileReader(file);
            BufferedReader reader = new BufferedReader(readable);
            try
            {
                String line = null;                
                while( (line = reader.readLine()) != null) 
                {
                    currentLine.setLength(0);
                    currentLine.append(line);

                    if(currentLine.toString().indexOf(".save") >= 0) 
                    {
                        currentDisk = otherDisks.request();
                        messageLog.add("U" + the_id + ": Requesting Disk " + (currentDisk+1));
                        currentFile = new StringBuffer(currentLine.substring(6));
                        String message = "D" + (currentDisk+1) + ": Saving File "+ currentFile.toString();
                        messageLog.add(message);
                    }
                    else if(currentLine.toString().indexOf(".print") >= 0) 
                    {
                        PrintJobThread newPJT = new PrintJobThread(new StringBuffer(currentLine.substring(7)), diskManager, printerManager, disks, printers, messageLog);
                        newPJT.start();
                    }
                    else if(currentLine.toString().indexOf(".end") >= 0)
                    {
                        String message3 = "D" + (currentDisk+1) + ": Saved File "+ currentFile.toString();
                        messageLog.add(message3);
                        otherDisks.release(currentDisk);
                        currentFile = null;
                        messageLog.add("D" + (currentDisk+1) + ": Now Available ");
                    }
                    else if (currentFile != null) 
                    {
                        Disk disk = disks[currentDisk];
                        FileInfo theFile = diskManager.getInfo(currentFile);
                        if(theFile == null) 
                        {
                            int sector = diskManager.nextSector(currentDisk);
                            theFile = new FileInfo(currentDisk, sector,1);
                            diskManager.createFile(currentFile, theFile);
                            disk.write(theFile.startingSector,currentLine);
                            Thread.sleep(200);
                        }
                        else 
                        {
                            int next = diskManager.nextSector(currentDisk);
                            theFile.fileLength = theFile.fileLength+1;
                            disk.write(next,currentLine);
                            Thread.sleep(200);
                        }                      
                    }
                }
                messageLog.add("U" + the_id + ": Is Finished!");
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}