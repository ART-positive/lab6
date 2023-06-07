package server.commands;


import server.core.Invoker;
import shared.core.exceptions.CommandParamsException;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.FileDoesNotExistException;
import shared.core.exceptions.RecursionException;

/**
 * The class contains an implementation of the remove_by_id command
 */
public class RemoveByIdCommand extends Command {

    public RemoveByIdCommand(Invoker invoker) {
        super(invoker);
    }

    @Override
    public String execute(Object args) throws RecursionException, FileAccessException, CommandParamsException, FileDoesNotExistException {

        if (invoker.getModelsManager().getModels().isEmpty()){
            return "Коллекция пуста!";
        }
        long id = (long)args;
        invoker.getModelsManager().removeById(id, invoker.getPrinter());
        return "Remove By Id команда выполнена!";
    }

    @Override
    public String getCommandInfo() {
        return String.format("Command \"remove_by_id <id>\": Эта команда удаляет модель с указанным id из коллекции." +
                "\nArguments: Integer(>0)");
    }
}
