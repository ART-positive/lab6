package server.core.datahandlers;


import com.thoughtworks.xstream.XStream;
import server.core.managers.ModelsManager;
import shared.core.models.MusicBand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Класс, отвечающий за сохранение текущей коллекции в xml-файл
 */
public class XMLWriter {

    private String filePath;

    public XMLWriter(){
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    /**
     * Метод, сохраняющий данные в формате xml, ИСПОЛЬЗУЕТСЯ СТОРОННЯЯ БИБЛИОТЕКА XStream
     *
     * @param musicBands коллекция, которую необходимо сохранить
     * @throws IOException возникает при невозможности записи в файл полученных данных
     */

    public void write(ModelsManager musicBands) throws IOException {
        File file = new File(filePath);
        String xmlText;
        if(musicBands.getModels().isEmpty()) {
            xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <set> \n </set>" ;
        }
        else {
            XStream xStream = new XStream();
            xStream.alias("musicBand", MusicBand.class);
            xStream.alias("set", ModelsManager.class);
            xStream.addImplicitCollection(ModelsManager.class, "musicBands");
            xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n " + xStream.toXML(musicBands.getModels()) ;
        }
        FileWriter writer = new FileWriter(file);
        writer.write(xmlText);
        writer.flush();
        writer.close();
    }

}

