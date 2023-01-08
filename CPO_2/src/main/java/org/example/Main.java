package org.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    private static final ArrayList<String> allPaths = new ArrayList<>();

    public static void getFilesFromFolder(File folder) {
        File[] folderEntries = folder.listFiles();
        if (folderEntries != null) {
            for (File entry : folderEntries) {
                if (entry.isDirectory()) {
                    getFilesFromFolder(entry);
                } else {
                    allPaths.add(entry.getPath());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Введите корневой каталог.");
        var in = new Scanner(System.in);
        var rootPath = in.nextLine();
        getFilesFromFolder(new File(rootPath));
        Collections.sort(allPaths);
        for (var path : allPaths) {
            System.out.print("для файла: ");
            System.out.println(path);
            try {
                MyFile file = new MyFile(path, rootPath);
                file.changeAndPrintFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}

