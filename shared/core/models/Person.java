package shared.core.models;

import server.core.Invoker;
import shared.core.exceptions.FieldValueIsNotCorrectException;
import server.core.validators.ModelsValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Person implements Serializable {
    private String name;
    private Float height;//Can be null, also >0
    private Country nationality;
    private EyeColor eyeColor; //Поле может быть null
    private HairColor hairColor; //Поле может быть null




    public Person( String name, Float height,  Country nationality,  EyeColor eyeColor, HairColor hairColor) {
        setName(name);
        setHeight(height);
        setNationality(nationality);
        setHairColor(hairColor);
        setEyeColor(eyeColor);
    }

    public String getName() {
        return name;
    }

    public Float getHeight() {
        return height;
    }

    public Country getNationality() {
        return nationality;
    }
    public EyeColor getEyeColor() {
        return eyeColor;
    }
    public HairColor getHairColor() {
        return hairColor;
    }

    public void setName(String name) {
        if (!name.isBlank() || Invoker.getIsDataLoading()){
            this.name = name;
        }
        else {
            throw new FieldValueIsNotCorrectException();
        }
    }

    public void setHeight(Float height) {
        if (height == null || height > 0 || Invoker.getIsDataLoading()){
            this.height = height;
        }
        else {
            throw new FieldValueIsNotCorrectException();
        }
    }

    public void setNationality(Country nationality) {
        if (nationality != null || Invoker.getIsDataLoading()){
            this.nationality = nationality;
        }
        else {
            throw new FieldValueIsNotCorrectException();
        }
    }

    public void setEyeColor(EyeColor eyeColor) {
        if (eyeColor == null || containsEyeColor(eyeColor) || Invoker.getIsDataLoading()){
            this.eyeColor = eyeColor;
        }
        else {
            throw new FieldValueIsNotCorrectException();
        }
    }

    public void setHairColor(HairColor hairColor) {
        if (hairColor == null || containsHairColor(hairColor) || Invoker.getIsDataLoading()){
            this.hairColor = hairColor;
        }
        else {
            throw new FieldValueIsNotCorrectException();
        }
    }

    public boolean containsEyeColor(EyeColor eyeColor) {

        for (EyeColor c1 : EyeColor.values()) {
            if (c1.equals(eyeColor)) {
                return true;
            }
        }
        return false;
    }
    public boolean containsHairColor(HairColor hairColor) {

        for (HairColor c1 : HairColor.values()) {
            if (c1.equals(hairColor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("---Name: %s\n---Height: %s\n---Nationality: %s\n---EyeColor: %s\n---HairColor: %s\n",
                name, ModelsValidator.fastNullCheck(height), nationality.toString(),
                ModelsValidator.fastNullCheck(eyeColor), ModelsValidator.fastNullCheck(hairColor));
    }

    public int compareTo(Person o) {
        return Comparator.comparing(Person::getHeight).compare(this, o);
    }

}
