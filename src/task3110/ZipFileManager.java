package task3110;

import exception.PathIsNotFoundException;
import exception.WrongZipFileException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileManager {
    private Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    //source - путь к файлу для архивирования
    //Создает zip архив с указанным путем в zipFile. Считывает и записывает в архив файл из source.
    public void createZip(Path source) throws Exception {
        if (Files.notExists(zipFile.getParent())) {
            Files.createDirectory(zipFile.getParent());
        }
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {

            if (Files.isRegularFile(source)) {
                addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
            } else if (Files.isDirectory(source)) {
                FileManager fm = new FileManager(source);
                List<Path> fileNames = fm.getFileList();
                for (Path fileName : fileNames) {
                    addNewZipEntry(zipOutputStream, source, fileName);
                }
            } else {
                throw new PathIsNotFoundException();
            }
        }
    }

    private void addNewZipEntry(ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws Exception {
        try (InputStream inputStream = Files.newInputStream(filePath.resolve(fileName))) {
            ZipEntry zipEntry = new ZipEntry(fileName.toString());
            zipOutputStream.putNextEntry(zipEntry);
            copyData(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }

    private void copyData(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    public List<FileProperties> getFilesList() throws Exception {
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }
        List<FileProperties> list = new ArrayList<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry tempEntry = zipInputStream.getNextEntry();
            while (tempEntry != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                //что бы узнать размер файла в архиве (объекта ZipEntry) его надо считать.
                // Не обязательно куда то, но обязательно просто считать.
                //здесь используется временный буфер, просто что бы было куда читать
                copyData(zipInputStream, out);
                list.add(new FileProperties(tempEntry.getName(), tempEntry.getSize(),
                        tempEntry.getCompressedSize(), tempEntry.getMethod()));
                tempEntry = zipInputStream.getNextEntry();
            }
        }
        return list;
    }

    //извлечение всех файлов из архива с сохранением структуры папок и файлов в папках.
    // Создает директории если они еще не существуют по пути outputFolder
    public void extractAll(Path outputFolder) throws Exception {
        if (!Files.isRegularFile(zipFile)) throw new WrongZipFileException();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            if (Files.notExists(outputFolder)) Files.createDirectories(outputFolder);

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                //если zipEntry не обычный файл (т.е. директория) то getName будет давать относительный путь.
                // Этот путь будем создавать и прибавлять к outputFolder.
                //путь с учетом вложенных папок. Эти же папки и досоздаются при необходимости
                Path newPath = outputFolder.resolve(zipEntry.getName());
                if (Files.notExists(newPath.getParent())) {
                    Files.createDirectories(newPath.getParent());
                }
                try (OutputStream outputStream = Files.newOutputStream(newPath)) {
                    copyData(zipInputStream, outputStream);
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    //удаление файлов из архива.
    //pathList - лист относительных путей файлов в архиве
    //UPD: от этого метода, да и некоторых других, моей реализации не осталось. Логика была верная и все работало как надо.
    // НО гребаный валидатор не хавал такие реализации.
    public void removeFiles(List<Path> pathList) throws Exception {
        if (!Files.isRegularFile(zipFile)) throw new WrongZipFileException();
        //эта директория будет перезаписана вместо старого архива
        Path tempZip = Files.createTempFile(null, null);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZip));
             ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry tempEntry = zipInputStream.getNextEntry();
            while (tempEntry != null) {
                if (!pathList.contains(Paths.get(tempEntry.getName()))) {
                    zipOutputStream.putNextEntry(new ZipEntry(tempEntry.getName()));
                    copyData(zipInputStream, zipOutputStream);
                    zipOutputStream.closeEntry();
                    zipInputStream.closeEntry();
                } else {
                    ConsoleHelper.writeMessage("Файл " + tempEntry.getName() + " удален");

                }
                tempEntry = zipInputStream.getNextEntry();
            }
        }
        Files.move(tempZip, zipFile, StandardCopyOption.REPLACE_EXISTING);

    }

    public void removeFile(Path path) throws Exception {
        removeFiles(Collections.singletonList(path));
    }

    public void addFiles(List<Path> absolutePathList) throws Exception {
        if (!Files.isRegularFile(zipFile)) throw new WrongZipFileException();
        Path tempZip = Files.createTempFile(null, null);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZip));
             ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            //список имен фалов из архива
            List<Path> currentFilesList = new ArrayList<>();
            //прохождение по существующему архиву и перезапись в новый
            ZipEntry tempEntry = zipInputStream.getNextEntry();
            while (tempEntry != null) {
                currentFilesList.add(Paths.get(tempEntry.getName()));
                zipOutputStream.putNextEntry(tempEntry);
                copyData(zipInputStream, zipOutputStream);
                zipInputStream.closeEntry();
                zipOutputStream.closeEntry();
                tempEntry = zipInputStream.getNextEntry();
            }
            //прохождение по списку добавляемых файлов. Проверка, существует ли данный файл в архиве или нет
            for (Path p : absolutePathList) {
                if (!Files.isRegularFile(p)) {
                    throw new PathIsNotFoundException();
                }
                if (currentFilesList.contains(p.getFileName())) {
                    ConsoleHelper.writeMessage("Файл " + p.getFileName() + " уже есть в архиве.");
                } else {
                    addNewZipEntry(zipOutputStream,p.getParent(),p.getFileName());
                    ConsoleHelper.writeMessage("Файл " + p.getFileName() + " добавлен в архив.");
                }
            }
        }
        Files.move(tempZip, zipFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public void addFile(Path absolutePath) throws Exception {
        addFiles(Collections.singletonList(absolutePath));
    }
}
