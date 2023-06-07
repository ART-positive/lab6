package server.commands;

import server.core.Invoker;
import shared.core.exceptions.CommandParamsException;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.FileDoesNotExistException;
import shared.core.exceptions.RecursionException;
import shared.core.models.MusicBand;
import shared.core.models.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * The class contains an implementation of the count_greater_than_front_man command
 */
public class PrintFieldAscendingFrontManCommand extends Command{

    public PrintFieldAscendingFrontManCommand(Invoker invoker) {
       super(invoker);
    }

    @Override
    public String execute(Object args) throws RecursionException, FileAccessException, CommandParamsException, FileDoesNotExistException {
        if (invoker.getModelsManager().getModels().isEmpty()){
            return "Коллекция пуста!";
        }
        List<Person> personList = new ArrayList<>();
        for (MusicBand musicBand : invoker.getModelsManager().getModels()) {
            personList.add(musicBand.getFrontMan());
        }
        StringBuilder str = new StringBuilder("\n");
        personList.sort(Person::compareTo);
        for (Person person : personList) {
            str.append(person).append("\n========================\n");
        }
        //invoker.getPrinter().print(String.format("Music bands with front men, that higher than %s: %s", height, count));
        return str + "Print Field Ascending Front Man команда выполнена!";
    }

    @Override
    public String getCommandInfo() {
        return String.format("Command \"print_field_ascending_front_man\": Эта команда позволяет вывести значения поля frontMan всех элементов в порядке возрастания.");
    }
}
