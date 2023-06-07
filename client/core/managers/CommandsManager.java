package client.core.managers;

import client.commands.*;
import client.core.Invoker;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains tools for storing, parsing and returning command instances.
 */
public class CommandsManager {
    private final Invoker invoker;
    private Map<String, Command> commandsCollection;

    public CommandsManager(Invoker invoker){
        this.invoker = invoker;
        initializeCommands();
    }

    /**
     * Return all allowed client.commands.
     * @return Map object with Command objects.
     */
    public Map<String, Command> getCommandsCollection() {
        return commandsCollection;
    }

    /**
     * Add client.commands to the HashMap.
     */
    private void initializeCommands() {
        commandsCollection = new HashMap<>();
        commandsCollection.put("help", new HelpCommand(invoker));
        commandsCollection.put("add", new AddCommand(invoker));
        commandsCollection.put("execute_script", new ExecuteScriptCommand(invoker));
        commandsCollection.put("show", new ShowCommand(invoker));
        commandsCollection.put("exit", new ExitCommand(invoker));
        commandsCollection.put("update", new UpdateCommand(invoker));
        commandsCollection.put("clear", new ClearCommand(invoker));
        commandsCollection.put("remove_by_id", new RemoveByIdCommand(invoker));
        commandsCollection.put("max_by_genre", new MaxByGenreCommand(invoker));
        commandsCollection.put("add_if_min", new AddIfMinCommand(invoker));
        commandsCollection.put("add_if_max", new AddIfMaxCommand(invoker));
        commandsCollection.put("remove_lower", new RemoveLowerCommand(invoker));
        commandsCollection.put("print_field_ascending_front_man", new PrintFieldAscendingFrontManCommand(invoker));
        commandsCollection.put("info", new InfoCommand(invoker));
    }

    /**
     * Separates the command from its arguments, retrieves the command object if it exists and passes it to the invoker.
     * @param line Command line
     */
    public void parseLine(String line){
        if (line.contains(" ")){
            String[] commandLine = line.split(" ",2);
            Command command = getCommand(commandLine[0]);
            if (command == null){
                return;
            }
            invoker.invokeCommand(command, commandLine[1]);
        }
        else{
            Command command = getCommand(line);
            if (command == null){
                return;
            }
            invoker.invokeCommand(command);
        }
    }

    /**
     //* Get command from the client.commands HashMap
     * @param line
     * @return Command object.
     */
    private Command getCommand(String line){
        if (commandsCollection.containsKey(line)){
            return commandsCollection.get(line);
        }
        invoker.getPrinter().print("Команда не существует!\nИспользуйте \"help\", для информации по всем командам.");
        return null;
    }
}
