import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
public class PrintJobThread extends Thread 
{
    private StringBuffer name;
    Disk[] disks;
    Printer[] printers;
    DiskManager diskManager;
    ResourceManager printerManager;
    ArrayList <String> messageLog;
    public PrintJobThread(StringBuffer fileName, DiskManager DM, ResourceManager PM, Disk[] d, Printer[] p, ArrayList <String> m) 
    {
        this.name = new StringBuffer(fileName);
        diskManager = DM;
        printerManager = PM;
        disks = d;
        printers = p;
        messageLog = m;
    }

    public void run() 
    {
        FileInfo info = diskManager.getInfo(name);
        Disk disk = disks[info.diskNumber];

        int printerIndex = printerManager.request();
        Printer printer = printers[printerIndex];
        
        messageLog.add("D" + (info.diskNumber +1) + ": Reading " + name.toString());      
        String message = "P" + (printerIndex+1) + ": Printing File " + name.toString();
        messageLog.add("D" + (info.diskNumber+1) + ": Now Available ");
        messageLog.add(message);

        StringBuffer sector = new StringBuffer();
        int i = 0;
        while(i < info.fileLength) 
        {
            sector.setLength(0);
            try 
            {
                int num = info.startingSector + i;
                disk.read(num,sector);
                Thread.sleep(200);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            
            try 
            {
                printer.print(sector);
                Thread.sleep(2750);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            i++;
        }
        
        String message2 = "P" + (printerIndex+1) + ": Finished Printing File "+ name.toString();
        messageLog.add(message2);
        
        printerManager.release(printerIndex);

        String message3 = "P" + (printerIndex+1) + ": Now Available";
        messageLog.add(message3);
    }
}