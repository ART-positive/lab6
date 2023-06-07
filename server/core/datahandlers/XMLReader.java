package server.core.datahandlers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import server.core.managers.ModelsManager;
import shared.core.models.MusicBand;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;

/**
 * Класс, отвечающий за стартовую обработку xml-файла с данными о коллекции
 */
public class XMLReader {

    public XMLReader(){
    }
    /**
     * Метод, преобразующий xml-файл в коллекцию драконов
     *
     * @param file файл, из которого происходит считывание
     * @return заполненная коллекцию HashSet
     * @throws IOException возникает при некорректных данных в файле или их неправильной интерпретации
     */

    public LinkedHashSet<MusicBand> read(File file) throws IOException {
        try {

            XStream xStream = new XStream();
            xStream.addPermission(AnyTypePermission.ANY);
            xStream.alias("musicBand", MusicBand.class);
            xStream.alias("linked-hash-set", ModelsManager.class);
            xStream.addImplicitCollection(ModelsManager.class, "musicBands");
            StringBuilder xmlText = new StringBuilder();
            FileReader reader = new FileReader(file);
            Scanner sc = new Scanner(reader);
            while (sc.hasNextLine()) {
                xmlText.append(sc.nextLine());
            }
            reader.close();

            ModelsManager musicBands = (ModelsManager) xStream.fromXML(xmlText.toString());
            return musicBands.getModels();
        } catch (XStreamException e) {
            FileWriter writer = new FileWriter(file);
            writer.write("");
            System.out.println("Неккоректный файл. Файл очищен");
            return new ModelsManager().getModels();
        }
    }

}
