package client.commands;

import client.connection.ThreadsBridgeHandler;
import shared.commands.commandsdtos.CommandDTO;
import shared.commands.enums.DataField;
import shared.connection.requests.CommandRequest;
import client.core.Invoker;
import shared.core.models.*;
import client.core.validators.CommandsDataValidator;
import shared.interfaces.IPrinter;
import shared.interfaces.Validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The class contains an implementation of the add command
 */
public class AddCommand extends Command {
    private final Invoker invoker;

    private final int LOCATION_X_INDEX = 0;
    private final int LOCATION_Y_INDEX = 1;
    private final int LOCATION_COORDINATES_COUNT = 3;
    private final int COORDINATE_Y_LIMIT = 742;

    public AddCommand(Invoker invoker){
        this.invoker = invoker;
    }
    @Override
    public void execute(String... arguments) {
        Map<DataField,Object> data = collectData();
        invoker.getConnection().getSender().send(new CommandRequest(new CommandDTO("AddCommand"), data));
        ThreadsBridgeHandler.waitCommandExecuted(invoker);
    }

    /**
     * The method is responsible for collecting data from the user.
     * @return Returns an object with data to create a model.
     */
    public Map<DataField, Object> collectData(){
        IPrinter printer = invoker.getPrinter();
        Map<DataField, Object> data = new HashMap<>();
        printer.print("Введите название группы:");
        data.put(DataField.NAME, CommandsDataValidator.nameCheck(invoker.getListener().nextLine(), invoker.getListener(), invoker.getPrinter(),false));

        printer.print("Введите координаты:");
        printer.print("--Введите X(целое число):");
        int x = (int)CommandsDataValidator.numbersCheck(invoker.getListener().nextLine(), invoker.getListener(), printer, Integer.class,false);
        Validator<Double> yValidator = n->n<=COORDINATE_Y_LIMIT;
        printer.print("--Введите Y(Double, <=742):");
        double y = (double)CommandsDataValidator.numbersCheck(invoker.getListener().nextLine(), invoker.getListener(), printer, Double.class,false, yValidator);
        data.put(DataField.COORDINATES, new Coordinates(x,y));

        Validator<Integer> numberOfParticipantsValidator = n->n>0;
        printer.print("Введите количество участников (должно быть > 0):");
        data.put(DataField.NUMBER_OF_PARTICIPANTS, CommandsDataValidator.numbersCheck(invoker.getListener().nextLine(), invoker.getListener(), printer, Integer.class,false, numberOfParticipantsValidator));

        printer.print("Укажите жанр музыки");
        printer.print("Доступные жанры: " + Arrays.toString(MusicGenre.values()));
        data.put(DataField.GENRE, (MusicGenre)CommandsDataValidator.enumCheck(invoker.getListener().nextLine(), invoker.getListener(), printer, MusicGenre.class, true));


        String name;
        Float height;
        Country nationality;
        EyeColor eyeColor;
        HairColor hairColor;
        printer.print("Введите параметры человека:");
        printer.print("--Введите имя человека (не null):");
        name = CommandsDataValidator.nameCheck(invoker.getListener().nextLine(),invoker.getListener(),printer,false);
        printer.print("--Введите рост человека (Float >0):");
        Validator<Float> heightValidator = n->n==null || n>0;
        height = (Float)CommandsDataValidator.numbersCheck(invoker.getListener().nextLine(),invoker.getListener(),printer,Float.class,true, heightValidator);
        printer.print("--Введите цвет глаз.");
        printer.print("--Доступные цвета: " + Arrays.toString(EyeColor.values()));
        eyeColor = CommandsDataValidator.enumCheck(invoker.getListener().nextLine(),invoker.getListener(),printer,EyeColor.class, true);
        printer.print("--Введите цвет волос.");
        printer.print("--Доступные цвета: " + Arrays.toString(HairColor.values()));
        hairColor = CommandsDataValidator.enumCheck(invoker.getListener().nextLine(),invoker.getListener(),printer,HairColor.class, true);
        printer.print("--Введите гражданство.");
        printer.print("--Доступные национальности: " + Arrays.toString(Country.values()));
        nationality = CommandsDataValidator.enumCheck(invoker.getListener().nextLine(),invoker.getListener(),printer,Country.class, false);
        Person person = new Person(name,height,nationality,eyeColor, hairColor);
        data.put(DataField.FRONTMAN, person);

        return data;
    }
}
