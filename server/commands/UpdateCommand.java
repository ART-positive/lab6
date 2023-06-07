package server.commands;

import server.core.validators.ModelsValidator;
import shared.commands.enums.DataField;
import shared.connection.requests.CommandRequest;
import shared.connection.requests.ValidationRequest;
import server.core.Invoker;
import shared.core.exceptions.CommandParamsException;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.FileDoesNotExistException;
import shared.core.exceptions.RecursionException;

import java.util.Map;

/**
 * The class contains an implementation of the update command
 */
public class UpdateCommand extends Command{
    private final int ID_INDEX = 1;
    private final int MAP_INDEX = 0;
    public UpdateCommand(Invoker invoker) {
        super(invoker);
    }

    @Override
    public String execute(Object args) throws RecursionException, FileAccessException, CommandParamsException, FileDoesNotExistException {
        long id = (long)((Object[])args)[ID_INDEX];
        Map<DataField, Object> data =  (Map<DataField, Object>)((Object[])args)[MAP_INDEX];
        System.out.println(invoker.getModelsManager().getUsedIDs());
        if(invoker.getModelsManager().getUsedIDs().contains(id)){
            invoker.getModelsManager().updateModel(id, data, invoker.getPrinter());
            return "Выполнено обновление модели!";
        }
        return "Модели с таким идентификатором не существует!";
    }

    @Override
    public String getCommandInfo() {
        return "Command \"update <id>\": Эта команда позволяет пользователю обновить существующие объекты в коллекции."
                + "\nArguments: Integer(>0)";
    }

    @Override
    public void validate(Object args) {
        long id = (long)args;
        if (invoker.getModelsManager().findModelById(id, invoker.getPrinter()) != null) {
            invoker.getConnection().send(new ValidationRequest(null, true));
        }
        else {
            invoker.getPrinter().print("Невозможно обновить.");
            invoker.getConnection().send(new ValidationRequest(null, false));
            //invoker.getConnection().send(new CommandRequest(null, true));
        }
    }
}
