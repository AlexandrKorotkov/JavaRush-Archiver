package task3110;

import exception.WrongZipFileException;

import java.io.IOException;


public class Archiver {
    public static void main(String[] args) {
        Operation operation = null;
        do {
            try {
                operation = askOperation();
                CommandExecutor.execute(operation);

            } catch (WrongZipFileException e) {
                ConsoleHelper.writeMessage("Вы не выбрали файл архива или выбрали неверный файл.");
            } catch (Exception e) {
                ConsoleHelper.writeMessage("Произошла ошибка. Проверьте введенные данные.");
            }
        } while (operation != Operation.EXIT);


    }

    public static Operation askOperation() throws IOException {
        ConsoleHelper.writeMessage("Введите номер операции:");
        ConsoleHelper.writeMessage(Operation.CREATE.ordinal() + " - создать архив");
        ConsoleHelper.writeMessage(Operation.ADD.ordinal() + " - добавить файл архив");
        ConsoleHelper.writeMessage(Operation.REMOVE.ordinal() + " - удалить файл из архива");
        ConsoleHelper.writeMessage(Operation.EXTRACT.ordinal() + " - распаковать архив");
        ConsoleHelper.writeMessage(Operation.CONTENT.ordinal() + " - просмотреть архив");
        ConsoleHelper.writeMessage(Operation.EXIT.ordinal() + " - выход");
        return Operation.values()[ConsoleHelper.readInt()];
    }
}
