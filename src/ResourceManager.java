public class ResourceManager
{
    boolean isFree[];
    public ResourceManager(int numberOfItems)
    {
        isFree = new boolean[numberOfItems];
        for (int i=0; i<isFree.length; ++i)
            isFree[i] = true;
    }

    synchronized int request()
    {
        while (true)
        {
            for (int i = 0; i < isFree.length; ++i)
            {
                if (isFree[i])
                {
                    isFree[i] = false;
                    return i;
                }
            }
            
            try 
            {
                this.wait(); // block until someone releases a Resource
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }

    synchronized void release( int index )
    {
        isFree[index] = true;
        this.notifyAll(); // let a waiting thread run
    }
}