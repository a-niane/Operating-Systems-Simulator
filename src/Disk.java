class Disk 
{
    static final int NUM_SECTORS = 1024;
    private int the_id;
    StringBuffer sectors[] = new StringBuffer[NUM_SECTORS];
    
    public Disk (int id)
    {
        the_id = id;
        for (int i=0; i <sectors.length; ++i)
        {
            sectors[i] = new StringBuffer(8); 
        }
    }
    
    void write(int sector, StringBuffer data) 
    {
        sectors[sector].setLength(0);
        sectors[sector].append(data);
    }
    
    void read(int sector, StringBuffer data) 
    {
        data.append(sectors[sector]);
    }
} 