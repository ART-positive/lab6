package server.core.managers;

import server.core.validators.ModelsValidator;
import shared.commands.enums.DataField;
import shared.core.models.*;
import shared.interfaces.IPrinter;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Contains tools to manage your collection.
 */
public class ModelsManager {
    private static final Logger logger = Logger.getLogger(ModelsManager.class.getName());
    private ArrayList<Long> usedIDs;
    private LinkedHashSet<MusicBand> musicBands;
    private String creationDate;
    private File outFile;


    public ModelsManager(LinkedHashSet<MusicBand> musicBands){
        this.musicBands = musicBands;
        getModelsIDs();
        creationDate = ZonedDateTime.now().toLocalDate().toString();
    }
    public ModelsManager(){
        this.musicBands = new LinkedHashSet<>();
        getModelsIDs();
        creationDate = ZonedDateTime.now().toLocalDate().toString();
    }

    /**
     * Метод, возвращающий текущий файл, в который будет производиться запись коллекции при сохранении
     *
     * @return файл, в который производится запись готовой коллекции
     */
    public File getOutFile() {
        return outFile;
    }

    /**
     * Метод, устанавливающий файл, в который будет производиться запись коллекции при сохранении
     *
     * @param outFile файл, в который будет производиться запись коллекции
     */
    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    /**
     * Creates new model with random ID.
     * @param data data Map for model's constructor.
     * @return new model.
     */
    public MusicBand createModel(Map<DataField, Object> data, IPrinter printer){
        printer.print("Начало создания объекта...");
        logger.log(Level.INFO, "Создание новой модели.");
        return new MusicBand(
                generateId(),
                (String)data.get(DataField.NAME),
                (Coordinates) data.get(DataField.COORDINATES),
                (int)data.get(DataField.NUMBER_OF_PARTICIPANTS),
                (MusicGenre)data.get(DataField.GENRE),
                (Person) data.get(DataField.FRONTMAN)
                );
    }

    /**
     * Creates new model with custom ID.
     * @param data data Map for model's constructor.
     * @param id Desired id for the model.
     * @return new model.
     */
    public MusicBand createModel(Map<DataField, Object> data, long id, IPrinter printer){
        printer.print("Начало создания объекта...");
        logger.log(Level.INFO, "Создание новой модели.");
        return new MusicBand(
                id,
                (String)data.get(DataField.NAME),
                (Coordinates) data.get(DataField.COORDINATES),
                (int)data.get(DataField.NUMBER_OF_PARTICIPANTS),
                (MusicGenre)data.get(DataField.GENRE),
                (Person) data.get(DataField.FRONTMAN)
        );
    }

    /**
     * Add models ArrayDeque to the collection.
     * @param queue Models collection.
     */
    public void addModels(LinkedHashSet<MusicBand> queue){
        musicBands.addAll(queue);
        queue.stream().map(MusicBand::getId).forEach(usedIDs::add);
        //sort();
    }
    /**
     * Add model to the collection.
     * @param model Model object.
     */
    public void addModels(MusicBand model){
        musicBands.add(model);
        usedIDs.add(model.getId());
    }

    /**
     * Get model from the collection by ID and recreate it.
     * @param id Model id.
     * @param data new model data.
     */
    public void updateModel(long id, Map<DataField, Object> data, IPrinter printer){
        logger.log(Level.INFO, "Обновление модели.");

        MusicBand model = findModelById(id, printer);
        model.setName((String)data.get(DataField.NAME));
        model.setCoordinates((Coordinates) data.get(DataField.COORDINATES));
        model.setNumberOfParticipants((int)data.get(DataField.NUMBER_OF_PARTICIPANTS));
        model.setGenre((MusicGenre)data.get(DataField.GENRE));
        model.setFrontMan((Person) data.get(DataField.FRONTMAN));
    }

    /**
     * Find model in the collection by id.
     * @param id model id.
     * @return object of model.
     */
    public MusicBand findModelById(Long id, IPrinter printer){
        if (musicBands.size()==0){
            printer.print("Коллекция пуста!");
            return null;
        }
        MusicBand[] acceptedModels = musicBands.stream().filter(x->x.getId() == id).toArray(MusicBand[]::new);
        if (acceptedModels.length == 0){
            printer.print("Не удается найти элемент с таким идентификатором.");
            return null;
        }
        return acceptedModels[0];
    }

    /**
     * create new id for creating/loading model.
     * @return new ID.
     */
    private long generateId(){
        Random rnd = new Random();
        long id = rnd.nextLong(Long.MAX_VALUE);
        while(usedIDs.contains(id)){
            id = rnd.nextLong();
        }
        usedIDs.add(id);
        return id;
    }

    /**
     * remove all elements from the collection.
     */
    public void removeAll(IPrinter printer){
        musicBands.stream().forEach(musicBands::remove);
        usedIDs.clear();
    }

    /**
     * Remove model from the collection by model id.
     * @param id model id.
     */
    public void removeById(long id, IPrinter printer){
        MusicBand musicBand = findModelById(id, printer);
        if (musicBand != null){
            logger.log(Level.INFO, "Удаление модели.");
            musicBands.remove(musicBand);
            usedIDs.remove(id);
            printer.print(String.format("Модель %s успешно удалена.", id));
        }
        else printer.print("Модель не существует!");
    }

    public void removeById(long id){
        for(MusicBand musicBand : musicBands) {
            if(musicBand.getId() == id) {
                musicBands.remove(musicBand);
                usedIDs.remove(id);
            }
        }
    }


    public LinkedHashSet<MusicBand> getModels() {
        return musicBands;
    }

    public MusicBand maxByGengre(){
        for(MusicBand musicBand : musicBands)
            if(musicBand.getGenre().equals(MusicGenre.PSYCHEDELIC_CLOUD_RAP)) return musicBand;
        for(MusicBand musicBand : musicBands)
            if(musicBand.getGenre().equals(MusicGenre.PROGRESSIVE_ROCK)) return musicBand;
        for(MusicBand musicBand : musicBands)
            if(musicBand.getGenre().equals(MusicGenre.HIP_HOP)) return musicBand;
        for(MusicBand musicBand : musicBands)
            if(musicBand.getGenre().equals(MusicGenre.BLUES)) return musicBand;
        for(MusicBand musicBand : musicBands)
            if(musicBand.getGenre().equals(MusicGenre.RAP)) return musicBand;
        return null;
    }
    public Long getMinID() {
        return Collections.min(usedIDs);
    }

    public Long getMaxID() {
        return Collections.max(usedIDs);
    }

    public ArrayList<Long> getUsedIDs() {
        return usedIDs;
    }

    public String getCreationDate() {
        return creationDate;
    }

    private void getModelsIDs(){
        usedIDs = new ArrayList<>();
        for(MusicBand musicBand : musicBands){
            usedIDs.add(musicBand.getId());
        }
    }
}
