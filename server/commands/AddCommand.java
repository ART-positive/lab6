package server.commands;

import server.core.Invoker;
import shared.commands.enums.DataField;

import java.util.Map;

/**
 * The class contains an implementation of the add command
 */
public class AddCommand extends Command {

    public AddCommand(Invoker invoker){
        super(invoker);
    }

    @Override
    public String execute(Object arguments) {
        Map<DataField,Object> data = (Map<DataField, Object>) arguments;
        invoker.getModelsManager().addModels(invoker.getModelsManager().createModel(data, invoker.getPrinter()));
        return "Объект успешно создан!";
    }

    @Override
    public String getCommandInfo(){
        return String.format("Command \"add\": Эта команда позволяет создать новую модель музыкальной группы. При вводе команды вам будут предложены поля для ввода.");
    }
}
