package server.commands;

import server.core.Invoker;
import server.core.validators.ModelsValidator;
import shared.commands.enums.DataField;
import shared.connection.requests.CommandRequest;
import shared.connection.requests.ValidationRequest;
import shared.core.exceptions.*;

import java.util.Map;

/**
 * The class contains an implementation of the add_if_min command.
 */
public class AddIfMinCommand extends Command {
    private final int MAP_INDEX = 0;

    private final int ID_INDEX = 1;


    public AddIfMinCommand(Invoker invoker) {
        super(invoker);
    }

    @Override
    public String execute(Object  args) throws RecursionException, FileAccessException, CommandParamsException, FileDoesNotExistException, ArgumentLimitsException {
        if (!invoker.getModelsManager().getUsedIDs().contains((long)((Object[])args)[ID_INDEX])){ //ModelsValidator.idValueCheck((long)((Object[])args)[ID_INDEX])
            invoker.getModelsManager().addModels(invoker.getModelsManager().createModel((Map<DataField, Object>)((Object[])args)[MAP_INDEX], (long)((Object[])args)[ID_INDEX], invoker.getPrinter()));
            invoker.getPrinter().print("Объект успешно создан!");
            return "Add If Min команда выполнена!";
        }
        return "Модель с таким идентификатором уже существует!";
    }

    @Override
    public String getCommandInfo() {
        return String.format("Command \"add_if_min <id>\": Эта команда создает новый элемент коллекции с указанным id, если он меньше, чем наименьший id в коллекции."
        +"\nArguments: Integer (>0)");
    }

    @Override
    public void validate(Object args) {
        if (invoker.getModelsManager().getModels().isEmpty()){
            invoker.getPrinter().print("Коллекция пуста!");
        }
        else if (invoker.getModelsManager().getMinID() > (long)args){
            invoker.getConnection().send(new ValidationRequest(null, true));
            return;
        }
        invoker.getPrinter().print("ID не является минимальным!");
        invoker.getConnection().send(new ValidationRequest(null, false));
        invoker.getConnection().send(new CommandRequest(null, true));

    }
}
