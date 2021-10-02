package task3110;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private Path rootPath;
    private List<Path> fileList;

    public FileManager(Path rootPath) throws IOException {
        this.rootPath = rootPath;
        fileList = new ArrayList<>();
        collectFileList(rootPath);
    }

    public List<Path> getFileList() {
        return fileList;
    }

    private void collectFileList(Path path) throws IOException {
        //добавление файлов с относительным именем
        if (Files.isRegularFile(path)) {
            fileList.add(rootPath.relativize(path));
        }
        //рекурсивный проход по вложенным файлам/папкам
        //сделано через DirectoryStream в соответсвии с заданием. Может переделать через FileVisitor?
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path p : directoryStream) {
                    collectFileList(p);
                }
            }
        }
    }
}
