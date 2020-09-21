package ru.somber.casesimulator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.somber.casesimulator.CaseRun;
import ru.somber.casesimulator.CaseStorage;
import ru.somber.casesimulator.ItemDrop;
import ru.somber.casesimulator.util.gson.GsonItemDeserializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final String pathToConfigFolder = "resource\\config\\";
    private static final String itemDropConfigFile = "itemdrop.json";

    private List<String> caseNames;


    public ConfigManager() {
        caseNames = new ArrayList<>();
        updateCaseNames();
    }

    public void updateCaseNames() {
        caseNames.clear();

        File configCaseDirectory = new File(pathToConfigFolder);
        String[] names = configCaseDirectory.list();

        if (names == null) {
            return;
        }

        for (String name : names) {
            File caseDirectory = new File(pathToConfigFolder, name);
            if (!caseDirectory.isDirectory()) {
                continue;
            }

            String[] fileCaseNames = caseDirectory.list((File dir, String fileName) -> {
                return fileName.equals(itemDropConfigFile);
            });

            if (fileCaseNames != null && fileCaseNames.length != 1) {
                continue;
            }

            caseNames.add(name);
        }
    }

    public List<String> getCaseNames() {
        return new ArrayList<>(caseNames);
    }

    public void loadCaseStorage(String caseName) {
        CaseStorage caseStorage = CaseRun.getCaseStorage();

        if (! caseNames.contains(caseName)) {
            return;
        }

        loadItemDropConfig(caseName, caseStorage);
    }

    private void loadItemDropConfig(String caseName, CaseStorage caseStorage) {
        ItemDrop[] itemDrops;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ItemDrop[].class, new GsonItemDeserializer());

        Gson gson = gsonBuilder.create();
        try {
            itemDrops = gson.fromJson(new FileReader("resource\\config\\" + caseName + "\\" + itemDropConfigFile), ItemDrop[].class);
            caseStorage.addItemDrops(itemDrops);

            System.out.println("Load item config for case: " + caseName);
            for (ItemDrop cd : itemDrops) {
                System.out.println(cd.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
