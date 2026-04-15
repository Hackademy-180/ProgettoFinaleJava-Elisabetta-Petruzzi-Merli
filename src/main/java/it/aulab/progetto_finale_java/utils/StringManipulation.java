package it.aulab.progetto_finale_java.utils;

public class StringManipulation {
    public static String getFileExtension(String nameFile){
        int dotIndex = nameFile.indexOf(".");
        String extension = nameFile.substring(dotIndex + 1);
        return extension;
    }
}
