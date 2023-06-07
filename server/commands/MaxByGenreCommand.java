package server.commands;

import server.core.Invoker;
import shared.core.exceptions.CommandParamsException;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.FileDoesNotExistException;
import shared.core.exceptions.RecursionException;
import shared.core.models.MusicBand;

/**
 * The class contains an implementation of the show command
 */
public class MaxByGenreCommand extends Command{

    public MaxByGenreCommand(Invoker invoker){
        super(invoker);
    }

    @Override
    public String execute(Object args) throws RecursionException, FileAccessException, CommandParamsException, FileDoesNotExistException {
        if (invoker.getModelsManager().getModels().isEmpty()){
            return "Коллекция пуста!";
        }
        //invoker.getModelsManager().getModels().stream().map(MusicBand::toString).forEach(invoker.getPrinter()::print);
        return invoker.getModelsManager().maxByGengre().toString();
    }

    @Override
    public String getCommandInfo() {
        return "Command \"max_by_genre\": Эта команда выводит объект коллекции, у которого значение поля genre является максимальным.";
    }
}
