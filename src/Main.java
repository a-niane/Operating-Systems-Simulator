import java.util.Scanner;
import java.io.File; 
import java.io.PrintWriter; 
import java.io.IOException; 
import java.util.ArrayList;
import java.io.*;

//gui imports
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.IOException;

//extra credit imports
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.*;
import java.awt.Desktop;
public class Main 
{
    static int numUsers;
    static int numDisks;
    static int numPrinters;
    static boolean startGui;

    static Disk[] disks;
    static Printer[] printers;
    static UserThread[] users;

    static DiskManager diskManager;
    static ResourceManager otherDisks;
    static ResourceManager printerManager;

    //gui variables
    static JLabel label, disk1Label, disk2Label, print1Label, print2Label, print3Label;
    static JLabel user1Label, user2Label, user3Label, user4Label;
    static JFrame frame;
    static JPanel panel;
    static JButton start, pause, unmute, mute, open1, open2, open3;
    static boolean on = false; //if simulation is on
    static boolean isMuted = false;
    
    static Slider speedControl;
    static ProgressBar progress = null;
    static int sliderSpeed = 4;
    static ArrayList <String> messageLog = new ArrayList <String>();
    static int logger = 0; //track of message log
    static boolean mouseDown = false;
    static boolean running = false;
    
    //sound and music
    static File file;
    static AudioInputStream stream;
    static AudioFormat format;
    static DataLine.Info info;
    static Clip poffinClip;
    static Clip evoClip;
    static File myPrinter1, myPrinter2, myPrinter3;

    public static void main (String [] args) 
    { 
        configure(args); //threads started in configure
    }

    private static void configure (String [] args) //constructor
    {
        //seperate parameters
        numUsers = 4; //Integer.parseInt(args[0].substring(1)); //4
        numDisks = 2; //Integer.parseInt(args[numUsers+1].substring(1)); //2
        numPrinters = 3; //Integer.parseInt(args[numUsers+2].substring(1)); //3
        startGui = true; //if there is no "-ng" 
        try
        {
            String noGui = args[numUsers+3];
            startGui = false; //it exists, no gui
        }
        catch (IndexOutOfBoundsException e)
        {
            startGui = true; //gui is run 
        }

        //create the array of users and printers
        disks = new Disk[numDisks];
        for(int i = 1; i <= disks.length; i++) 
        {
            disks[i-1] = new Disk(i);
        }

        printers = new Printer[numPrinters];
        for(int i = 1; i <= printers.length; i++) 
        {
            printers[i-1] = new Printer(i);
        }

        //Managers
        diskManager = new DiskManager(numDisks);
        printerManager = new ResourceManager(numPrinters); 
        otherDisks = new ResourceManager(numDisks);

        users = new UserThread[numUsers];
        for(int i = 1; i <= users.length; i++) 
        {
            users[i-1] = new UserThread(i, diskManager, otherDisks, printerManager, disks, printers, messageLog);
        }

        for (int i = 0; i < numUsers; ++i)
        {
            users[i].start();
        }

        try
        {
            Thread.sleep(150000);
        }
        catch (InterruptedException err)
        {
            err.printStackTrace();
        }
        logger = 0;

        if (startGui) 
        {
            initializeGUI(); //set up pictures and buttons

            //Aesthetics
            ImageIcon logo = new ImageIcon("resources/Logo.png");
            frame.setIconImage(logo.getImage()); 
            frame.getContentPane().setBackground(Color.gray);
            
            
            playPoffins(); //creates background song
            playEvo(); //congrats theme

            //buttons and ActionListener
            ActionListener a = new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) //equivalent of start button
                    {
                        if (!on && e.getSource() == start) //if start is clicked and wasn't on
                        { 
                            on = true; //start now turns it on
                            start.setEnabled(false);
                            pause.setEnabled(true);
                            mute.setEnabled(true); //able to mute music
                            poffinClip.start(); //start bgm
                        }
                        else if (on && e.getSource() == pause) //if pause clicked and was on
                        {
                            on = false; //pause turns it off
                            start.setEnabled(true);
                            pause.setEnabled(false);
                            poffinClip.stop();
                            mute.setEnabled(false); //can't mute if paused
                            unmute.setEnabled(false); //same as above
                            //pause all movement, stop music
                        }
                    }
                };

            start.addActionListener(a);
            pause.addActionListener(a);  

            frame.add(start);
            frame.add(pause);
            
            ActionListener b = new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) //mute or unmute bgm 
                    {
                        if (on) //(sim must be on)
                        {
                            if(poffinClip.isRunning() && e.getSource() == mute) //if mute is selected
                            {
                                isMuted = true;
                                poffinClip.stop(); //stop track
                                mute.setEnabled(false);
                                unmute.setEnabled(true);
                            }
                            else if(!poffinClip.isRunning() && e.getSource() == unmute)
                            {
                                isMuted = false;
                                poffinClip.start(); //continue track
                                mute.setEnabled(true);
                                unmute.setEnabled(false);
                            }
                        }
                    }
                };

            unmute.addActionListener(b);
            mute.addActionListener(b);
            frame.add(unmute);
            frame.add(mute);
            
            ActionListener c = new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) //open printer files 
                    {
                        if (e.getSource() == open1) //all buttons can be pressed
                        {
                            myPrinter1 = new File("PRINTER1");
                            try
                            {
                                Desktop.getDesktop().open(myPrinter1);
                            }
                            catch (Exception err)
                            {
                                System.out.print("Supported: " + Desktop.isDesktopSupported());
                                err.printStackTrace();
                            }
                            //open1.setEnabled(false);
                        }
                        else if (e.getSource() == open2)
                        {
                            myPrinter2 = new File("PRINTER2");
                            try
                            {
                                Desktop.getDesktop().open(myPrinter2);
                            }
                            catch (Exception err)
                            {
                                err.printStackTrace();
                            }
                            //open2.setEnabled(false);
                        }
                        else if (e.getSource() == open3)
                        {
                            myPrinter3 = new File("PRINTER3");
                            try
                            {
                                Desktop.getDesktop().open(myPrinter3);
                            }
                            catch (Exception err)
                            {
                                err.printStackTrace();
                            }
                            //open3.setEnabled(false);
                        }
                    }
                };
                
            open1.addActionListener(c);
            open2.addActionListener(c);
            open3.addActionListener(c);
            frame.add(open1);
            frame.add(open2);
            frame.add(open3);

            //Panels show buttons and slider
            panel = new JPanel();
            panel.setBackground(Color.darkGray);
            panel.setBounds(0,0, 1600, 50);
            panel.add(start);
            panel.add(label);
            panel.add(pause);

            frame.addMouseListener(new MouseAdapter() 
                {
                    public void mousePressed(MouseEvent e)
                    {
                        if (e.getButton() == MouseEvent.BUTTON1) //if left key clicked
                        {
                            mouseDown = true;
                            startAThread();
                        }
                    }

                    public void mouseReleased(MouseEvent e)
                    {
                        if (e.getButton() == MouseEvent.BUTTON1) //if left key released 
                        {
                            mouseDown = true;
                        }
                    }
                });

            frame.add(panel);
            speedControl = new Slider();
            progress = new ProgressBar();
        }
    }
    
    private static void playPoffins()
    {
        String title = "resources/Song.wav";
        try
        {
            file = new File(title);
            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            poffinClip = (Clip) AudioSystem.getLine(info);
            poffinClip.open(stream);
        }
        catch (Exception err)
        {
            err.printStackTrace();
        }
    }
    
    private static void playEvo()
    {
        String title = "resources/Tada.wav";
        try
        {
            file = new File(title);
            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            evoClip = (Clip) AudioSystem.getLine(info);
            evoClip.open(stream);
        }
        catch (Exception err)
        {
            err.printStackTrace();
        }
    }

    private static void initializeGUI()
    {
        frame = new JFrame();
        ImageIcon disk1 = new ImageIcon("resources/Disk1.png");
        ImageIcon disk2 = new ImageIcon("resources/Disk2.png");
        ImageIcon printer1 = new ImageIcon("resources/Printer1.png");
        ImageIcon printer2 = new ImageIcon("resources/Printer2.png");
        ImageIcon printer3 = new ImageIcon("resources/Printer3.png");
        ImageIcon startIcon = new ImageIcon("resources/Start.png");
        ImageIcon pauseIcon = new ImageIcon("resources/Pause.png");
        ImageIcon user1 = new ImageIcon("resources/User 1.png");
        ImageIcon user2 = new ImageIcon("resources/User 2.png");
        ImageIcon user3 = new ImageIcon("resources/User 3.png");
        ImageIcon user4 = new ImageIcon("resources/User 4.png");
        ImageIcon legend = new ImageIcon("resources/Legend.png");
        ImageIcon on = new ImageIcon("resources/On.png");
        ImageIcon off = new ImageIcon("resources/Off.png");

        label = new JLabel("141OS Simulation        ");
        disk1Label = new JLabel("Disk 1: Now Available"); 
        disk2Label = new JLabel("Disk 2: Now Available");
        print1Label = new JLabel("Printer 1: Now Available");
        print2Label = new JLabel("Printer 2: Now Available"); 
        print3Label = new JLabel("Printer 3: Now Available");
        user1Label = new JLabel("User 1: Waiting");
        user2Label = new JLabel("User 2: Waiting");
        user3Label = new JLabel("User 3: Waiting");
        user4Label = new JLabel("User 4: Waiting");
        JLabel sub = new JLabel();

        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.TOP);
        label.setForeground(Color.cyan);
        
        sub.setIcon(legend);
        sub.setBounds(1350, 625, 210, 210); 

        user1Label.setIcon(user1);
        user1Label.setHorizontalTextPosition(JLabel.CENTER); 
        user1Label.setVerticalTextPosition(JLabel.TOP);
        user1Label.setForeground(Color.red);
        user1Label.setBounds(100, 50, 250, 250);

        user2Label.setIcon(user2);
        user2Label.setHorizontalTextPosition(JLabel.CENTER); 
        user2Label.setVerticalTextPosition(JLabel.TOP);
        user2Label.setForeground(Color.red);
        user2Label.setIconTextGap(-10);
        user2Label.setBounds(100, 225, 250, 250);

        user3Label.setIcon(user3);
        user3Label.setHorizontalTextPosition(JLabel.CENTER); 
        user3Label.setVerticalTextPosition(JLabel.TOP);
        user3Label.setForeground(Color.red);
        user3Label.setIconTextGap(-10);
        user3Label.setBounds(110, 400, 250, 250);

        user4Label.setIcon(user4);
        user4Label.setHorizontalTextPosition(JLabel.CENTER); 
        user4Label.setVerticalTextPosition(JLabel.TOP);
        user4Label.setForeground(Color.red);
        user4Label.setIconTextGap(-10);
        user4Label.setBounds(90, 575, 250, 250);

        disk1Label.setIcon(disk1);
        disk1Label.setHorizontalTextPosition(JLabel.CENTER); //left or right
        disk1Label.setVerticalTextPosition(JLabel.TOP); //top, center, bottom
        disk1Label.setForeground(Color.green); //changes to red when busy
        disk1Label.setBounds(600, 150, 250, 250);

        disk2Label.setIcon(disk2);
        disk2Label.setHorizontalTextPosition(JLabel.CENTER); //left or right
        disk2Label.setVerticalTextPosition(JLabel.TOP); //top, center, bottom
        disk2Label.setForeground(Color.green);
        disk2Label.setBounds(600, 450, 250, 250);

        print1Label.setIcon(printer1);
        print1Label.setHorizontalTextPosition(JLabel.CENTER); //left or right
        print1Label.setVerticalTextPosition(JLabel.TOP); //top, center, bottom
        print1Label.setForeground(Color.green);
        print1Label.setIconTextGap(-10);
        print1Label.setBounds(1100, 50, 250, 250);

        print2Label.setIcon(printer2);
        print2Label.setHorizontalTextPosition(JLabel.CENTER); //left or right
        print2Label.setVerticalTextPosition(JLabel.TOP); //top, center, bottom
        print2Label.setForeground(Color.green);
        print2Label.setIconTextGap(-10);
        print2Label.setBounds(1100, 300, 250, 250);

        print3Label.setIcon(printer3);
        print3Label.setHorizontalTextPosition(JLabel.CENTER); //left or right
        print3Label.setVerticalTextPosition(JLabel.TOP); //top, center, bottom
        print3Label.setForeground(Color.green);
        print3Label.setIconTextGap(-10);
        print3Label.setBounds(1100, 550, 250, 250);

        frame.add(user1Label);
        frame.add(user2Label);
        frame.add(user3Label);
        frame.add(user4Label);
        frame.add(disk1Label);
        frame.add(disk2Label);
        frame.add(print1Label);
        frame.add(print2Label);
        frame.add(print3Label);
        frame.add(sub);

        frame.setTitle("141OS GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.pack();

        start = new JButton("Start!");
        pause = new JButton("Pause!");
        unmute = new JButton("Unmute!"); //unmute
        mute = new JButton("Mute!"); //mute
        open1 = new JButton("Open Printer 1");
        open2 = new JButton("Open Printer 2");
        open3 = new JButton("Open Printer 3");
        start.setBounds(0, 20, 80, 80);
        pause.setBounds(1100, 20, 80, 80);
        unmute.setBounds(5, 5, 130, 35);
        mute.setBounds(145, 5, 110, 35);
        open1.setBounds(1350, 150, 160, 40);
        open2.setBounds(1350, 350, 160, 40);
        open3.setBounds(1350, 550, 160, 40);
        
        start.setIcon(startIcon);
        pause.setIcon(pauseIcon);
        unmute.setIcon(on);
        mute.setIcon(off);
        pause.setEnabled(false);
        unmute.setEnabled(false);
        mute.setEnabled(false);
        open1.setEnabled(false);
        open2.setEnabled(false);
        open3.setEnabled(false);
    }

    private static synchronized boolean check()
    {
        if(running)
        {
            return false;
        }
        else
        {
            running = true;
            return true;
        }
    }

    private static void startAThread()
    {
        if(check())
        {
            new Thread()
            {
                public void run()
                {
                    do
                    {
                        changeText();
                    } while(mouseDown);
                    running = false;
                }
            }.start();
        }
    }

    private static void changeText()
    {
        if (on && logger < messageLog.size())
        {         
            String text = messageLog.get(logger);
            if (text.indexOf("D1") >= 0) //disks
            {
                disk1Label.setText("Disk 1: " + text.substring(4));
                if (text.indexOf("Now") >= 0) //available = green
                {                    
                    disk1Label.setForeground(Color.green);
                }
                else if (text.indexOf("Saved") >= 0) //saved = blue
                {
                    disk1Label.setForeground(Color.blue);
                }
                else //saving = red
                {
                    disk1Label.setForeground(Color.red);
                }
            }
            else if (text.indexOf("D2") >= 0) 
            {
                disk2Label.setText("Disk 2: " + text.substring(4));
                if (text.indexOf("Now") >= 0) //available = green
                {                   
                    disk2Label.setForeground(Color.green);
                }
                else if (text.indexOf("Saved") >= 0) //saved = blue
                {
                    disk2Label.setForeground(Color.blue);
                }
                else //saving = red
                {
                    disk2Label.setForeground(Color.red);
                }
            }
            else if (text.indexOf("P1") >= 0)
            {
                print1Label.setText("Printer 1: " + text.substring(4));
                if (text.indexOf("Now") >= 0) //available = green
                {
                    print1Label.setForeground(Color.green);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    print1Label.setForeground(Color.blue);
                }
                else //finished = red
                {
                    print1Label.setForeground(Color.red);
                }
            }
            else if (text.indexOf("P2") >= 0)
            {
                print2Label.setText("Printer 2: " + text.substring(4));
                if (text.indexOf("Now") >= 0) //available = green
                {
                    print2Label.setForeground(Color.green);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    print2Label.setForeground(Color.blue);
                }
                else //finished = red
                {
                    print2Label.setForeground(Color.red);
                }
            }
            else if (text.indexOf("P3") >= 0)
            {
                print3Label.setText("Printer 3: " + text.substring(4));
                if (text.indexOf("Now") >= 0) //available = green
                {
                    print3Label.setForeground(Color.green);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    print3Label.setForeground(Color.blue);
                }
                else //finished = red
                {
                    print3Label.setForeground(Color.red);
                }
            }
            else if (text.indexOf("U1") >= 0)
            {
                user1Label.setText("User 1: " + text.substring(4));
                if (text.indexOf("Requested") >= 0) //requesting = red
                {
                    user1Label.setForeground(Color.red);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    user1Label.setForeground(Color.blue);
                }
            }
            else if (text.indexOf("U2") >= 0)
            {
                user2Label.setText("User 2: " + text.substring(4));
                if (text.indexOf("Requested") >= 0) //requesting = red
                {
                    user2Label.setForeground(Color.red);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    user2Label.setForeground(Color.blue);
                }
            }
            else if (text.indexOf("U3") >= 0)
            {
                user3Label.setText("User 3: " + text.substring(4));
                if (text.indexOf("Requested") >= 0) //requesting = red
                {
                    user3Label.setForeground(Color.red);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    user3Label.setForeground(Color.blue);
                }
            }
            else if (text.indexOf("U4") >= 0)
            {
                user4Label.setText("User 4: " + text.substring(4));
                if (text.indexOf("Requested") >= 0) //requesting = red
                {
                    user4Label.setForeground(Color.red);
                }
                else if (text.indexOf("Finished") >= 0) //finished = blue
                {
                    user4Label.setForeground(Color.blue);
                }
            }

            try
            {
                if (sliderSpeed != 0)
                {
                    int sleepTime = (int) (1000/(0.2*sliderSpeed));
                    Thread.sleep(sleepTime);
                    if(progress != null)
                        progress.bar.setValue(logger);
                    logger++;
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        frame.repaint();

        if (progress != null && logger >= messageLog.size())
        {
            progress.bar.setValue(logger);
            progress.bar.setString("All Threads Completed!");
            progress.bar.setForeground(Color.green);
            pause.setEnabled(false);
            unmute.setEnabled(false);
            mute.setEnabled(false);
            open1.setEnabled(true);
            open2.setEnabled(true);
            open3.setEnabled(true);
            if (!isMuted)
            {
                poffinClip.stop();
                evoClip.start();
            }
        }
    }
}

class Slider implements ChangeListener
{
    JFrame frame = Main.frame;
    JPanel panel = Main.panel;
    JLabel label;
    JSlider slider;
    Slider()
    {
        label = new JLabel("Speed Control");
        label.setHorizontalTextPosition(JLabel.CENTER); //left or right
        label.setVerticalTextPosition(JLabel.TOP);
        label.setForeground(Color.gray);
        slider = new JSlider(0, 10, 5); //1x speed to 10x speed (5 is normal) 

        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);

        slider.setPaintTrack(true);
        slider.setMajorTickSpacing(2);

        slider.setPaintLabels(true);
        slider.addChangeListener(this);

        panel.add(slider);

        panel.add(label);
        frame.add(panel);
    }

    public void stateChanged(ChangeEvent e)
    {
        Main.sliderSpeed = slider.getValue();
    }
}

class ProgressBar
{
    JFrame frame = Main.frame;
    int counter = 1;
    int limit = Main.messageLog.size();
    JProgressBar bar; 
    ProgressBar()
    {
        bar = new JProgressBar(0, limit);
        bar.setValue(0);
        bar.setBounds(300, 800, 1000, 20);
        bar.setStringPainted(true);
        frame.add(bar);
    }
}
