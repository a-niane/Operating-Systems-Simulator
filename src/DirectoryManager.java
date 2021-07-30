import java.util.Hashtable;
public class DirectoryManager 
{
    private Hashtable<String,FileInfo> files = new Hashtable<String, FileInfo>();

    public void enter(StringBuffer fileName, FileInfo info) 
    {
        String name = fileName.toString();
        files.put(name,info);
    }

    public FileInfo lookup(StringBuffer fileName) 
    {
        String name = fileName.toString();
        return files.get(name);
    }
}