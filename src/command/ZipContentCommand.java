package command;

import task3110.ConsoleHelper;
import task3110.FileProperties;
import task3110.ZipFileManager;

public class ZipContentCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Просмотр содержимого архива.");
        ZipFileManager zipFileManager = getZipFileManager();
        ConsoleHelper.writeMessage("Содержимое архива:");
        for (FileProperties f : zipFileManager.getFilesList()) {
            System.out.println(f);
        }
        ConsoleHelper.writeMessage("Содержимое архива прочитано.");
    }
}
