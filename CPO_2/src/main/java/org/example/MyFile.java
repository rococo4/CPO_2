package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public final class MyFile {
    private final String path;

    private final String rootPath;

    public MyFile(String path, String rootPath) throws IOException {
        this.path = path;
        this.rootPath = rootPath;
    }

    private void findCycles(String beginPath) throws IOException {
        ArrayList<MyFile> requiredFiles;
        requiredFiles = findRequiredFiles();
        if (requiredFiles.isEmpty()) {
            return;
        }
        for (var file : requiredFiles) {
            if (Objects.equals(file.path, beginPath)) {
                throw new IOException("Cycle was found.");
            }
            file.findCycles(beginPath);
        }
    }

    private ArrayList<MyFile> findRequiredFiles() throws IOException {
        ArrayList<MyFile> requiredPaths = new ArrayList<>();
        File f = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                int index = line.indexOf("require '");
                if (index != -1) {
                    requiredPaths.add(new MyFile(rootPath + line.substring(line.indexOf("'") + 1,
                            line.lastIndexOf("'")), rootPath));
                }
            }

        }
        return requiredPaths;
    }

    private void editAndPrintFile() throws IOException {
        var firstFile = new File("buffer1.txt");
        copyFile(path,"buffer1.txt");
        var secondFile = new File("buffer2.txt");
        System.out.println("Все требуемые пути:");
        while (true) {
            try (BufferedReader br = new BufferedReader(new FileReader(firstFile))) {
                try (var bw = new BufferedWriter(new FileWriter(secondFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        int index = line.indexOf("require '");
                        if (index != -1) {
                            var requiredPath = rootPath + line.substring(line.indexOf("'") + 1,
                                    line.lastIndexOf("'"));
                            System.out.println(requiredPath);
                            try (var br1 = new BufferedReader(new FileReader(requiredPath))) {
                                String buffer_line;
                                while ((buffer_line = br1.readLine()) != null) {
                                    bw.write(buffer_line);
                                    bw.append('\n');
                                }
                            }
                        } else {
                            bw.write(line);
                            bw.append('\n');
                        }
                    }
                }
            }
            if (!areThereStillRequirements(secondFile.getPath())) {
                System.out.println("Содержимое: ");
                System.out.print(new MyFile(secondFile.getPath(), rootPath));
                break;
            }
            var temp = firstFile;
            firstFile = secondFile;
            secondFile = temp;
        }
    }

    private boolean areThereStillRequirements(String path) throws IOException {
        File f = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                int index = line.indexOf("require '");
                if (index != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void copyFile(String fromPath, String toPath) throws IOException {
        var fromFile = new File(fromPath);
        var toFile = new File(toPath);
        try (BufferedReader br = new BufferedReader(new FileReader(fromFile))) {
            try (var bw = new BufferedWriter(new FileWriter(toFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.append('\n');
                }
            }
        }
    }

    public void changeAndPrintFile() throws IOException {
        findCycles(path);
        editAndPrintFile();

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        File f = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        result.append('\n');
        return result.toString();
    }

}
