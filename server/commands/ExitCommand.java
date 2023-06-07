package server.commands;

import server.core.Invoker;
import server.core.datahandlers.XMLWriter;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.RecursionException;

import java.io.IOException;

/**
 * The class contains an implementation of the save command
 */
public class ExitCommand extends Command {
    public ExitCommand(Invoker invoker) {
        super(invoker);
    }

    @Override
    public String execute(Object arguments) throws RecursionException, FileAccessException, IOException {
        invoker.getWriter().write(invoker.getModelsManager());
        return "Коллекция успешно сохранена.";
    }

    @Override
    public String getCommandInfo() {
        return "Command \"exit\": Эта команда завершает работу программы, предлагая вам сохранить изменения.";
    }
}
