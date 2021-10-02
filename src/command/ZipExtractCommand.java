package command;

import exception.WrongZipFileException;
import task3110.ConsoleHelper;
import task3110.ZipFileManager;

import java.nio.file.Paths;

public class ZipExtractCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        try {
            ConsoleHelper.writeMessage("Распаковка архива.");
            ZipFileManager zipFileManager = getZipFileManager();
            ConsoleHelper.writeMessage("Введите директорию куда распаковать архив.");
            zipFileManager.extractAll(Paths.get(ConsoleHelper.readString()));
            ConsoleHelper.writeMessage("Архив распакован.");
        } catch (WrongZipFileException e){
            ConsoleHelper.writeMessage("Вы неверно указали имя файла или директории.");
        }
    }
}
