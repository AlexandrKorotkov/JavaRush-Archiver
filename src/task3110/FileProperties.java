package task3110;

public class FileProperties {
    private String name;
    private long size, compressedSize;
    private int compressionMethod;

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public FileProperties(String name, long size, long compressedSize, int compressMethod) {
        this.name = name;
        this.size = size;
        this.compressedSize = compressedSize;
        this.compressionMethod = compressMethod;
    }

    public long getCompressionRatio() {
        return 100 - ((compressedSize * 100) / size);
    }

    @Override
    public String toString() {
        if(size>0){
            return name+" "+size/1024+" Kb ("+compressedSize/1024+" Kb) сжатие:"+getCompressionRatio()+"%";
        }return name;
    }
}
