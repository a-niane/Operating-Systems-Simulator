public class DiskManager extends ResourceManager
{
    private DirectoryManager DM;
    private int[] sectors;

    public DiskManager(int numberOfDisks) 
    {
        super(numberOfDisks);
        DM = new DirectoryManager();
        sectors = new int[numberOfDisks];
    }

    public int nextSector(int index) 
    {
        int next = sectors[index];
        sectors[index] = sectors[index]+1;
        return next;
    }

    synchronized public FileInfo getInfo(StringBuffer fileName) 
    {
        return DM.lookup(fileName);
    }

    synchronized public void createFile(StringBuffer fileName, FileInfo file) 
    {
        DM.enter(fileName, file);
    }
}