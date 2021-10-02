package command;

import task3110.ConsoleHelper;
import task3110.ZipFileManager;

import java.nio.file.Paths;

public class ZipRemoveCommand  extends ZipCommand{
    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Удаляем из архива.");
        ZipFileManager zipFileManager = getZipFileManager();
        ConsoleHelper.writeMessage("Введите какой файл удаляем");
        zipFileManager.removeFile(Paths.get(ConsoleHelper.readString()));
        ConsoleHelper.writeMessage("Архив изменен.");
    }
}
