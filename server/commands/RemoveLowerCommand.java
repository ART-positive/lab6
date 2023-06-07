package server.commands;


import server.core.Invoker;
import shared.core.exceptions.CommandParamsException;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.FileDoesNotExistException;
import shared.core.exceptions.RecursionException;
import shared.core.models.MusicBand;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Класс содержит реализацию команды remove_lower
 */
public class RemoveLowerCommand extends Command {

    public RemoveLowerCommand(Invoker invoker) {
        super(invoker);
    }

    @Override
    public String execute(Object args) throws RecursionException, FileAccessException, CommandParamsException, FileDoesNotExistException {

        if (invoker.getModelsManager().getModels().isEmpty()){
            return "Коллекция пуста!";
        }
        long id = (long)args;
        int k = 0;
        LinkedHashSet<MusicBand> musicBands = new LinkedHashSet();
        musicBands = invoker.getModelsManager().getModels();
        ArrayList<Long> deleteId = new ArrayList<>();
        for (MusicBand musicBand : musicBands){
            if(musicBand.getId() < id) {
                deleteId.add(musicBand.getId());
                k++;
            }
        }
        for(Long iid : deleteId) {
            invoker.getModelsManager().removeById(iid, invoker.getPrinter());
        }
        return "Remove Lower команда выполнена, удаленно " + k + " элемент_а(ов) коллекции!";
    }

    @Override
    public String getCommandInfo() {
        return String.format("Command \"remove_lower <id>\": Эта команда удаляет все элементы коллекции с меньшим id, чем заданный." +
                "\nArguments: Integer(>0)");
    }
}
