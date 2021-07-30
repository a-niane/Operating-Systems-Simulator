class FileInfo 
{
    int diskNumber;
    int startingSector;
    int fileLength;
    FileInfo()
    {
        diskNumber = 0;
        startingSector = 0;
        fileLength = 0;
    }
    
    FileInfo (int num, int sector, int length)
    {
        diskNumber = num;
        startingSector = sector;
        fileLength = length;
    }
} 