package ru.somber.casesimulator;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import ru.somber.casesimulator.menu.CaseMode;
import ru.somber.casesimulator.menu.CaseSettingsContainer;
import ru.somber.casesimulator.tape.DropController;
import ru.somber.casesimulator.util.ConfigManager;
import ru.somber.casesimulator.util.Helper;
import ru.somber.opengl.texture.Texture;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CaseRun {
    public static final int FPS = 150;
    public static final int COUNT_ITEM_IN_WIDTH = 7;

    private static CaseStorage caseStorage;
    private static DropController dropController;
    private static CaseRender caseRender;
    private static ConfigManager configManager;
    private static boolean isAlive;
    private static int tick;
    private static int itemRenderSide;

    private CaseSettingsContainer caseSettingsContainer;


    public CaseRun() {}

    public void setCaseSettings(CaseSettingsContainer csc) {
        caseSettingsContainer = csc;

    }

    public void start() throws Exception {
        init();

        isAlive = true;
        tick = 1;

        while ((! Display.isCloseRequested()) && isAlive) {
            tick++;

             update();
            render();

            Display.update();
            Display.sync(FPS);
        }

        destroy();
    }


    private void init() throws Exception {
        System.out.println(caseSettingsContainer.toString());

        String caseName = caseSettingsContainer.getCaseName();
        CaseMode caseMode = caseSettingsContainer.getCaseMode();
        int testCaseIteration = caseSettingsContainer.getCountTestIteration();
        long hashKeyRandom = caseSettingsContainer.getKeyRandom().hashCode();

        caseStorage = new CaseStorage();
        caseStorage.setKeyRandom(hashKeyRandom);
        configManager.loadCaseStorage(caseName);

        itemRenderSide = Display.getDesktopDisplayMode().getHeight() / 10;
        int heightInItem = caseStorage.countItemDrop() / COUNT_ITEM_IN_WIDTH + 1;
        if (caseStorage.countItemDrop() % COUNT_ITEM_IN_WIDTH != 0) {
            heightInItem++;
        }

        dropController = new DropController(caseStorage, caseMode);
        caseRender = new CaseRender(caseStorage, caseMode, COUNT_ITEM_IN_WIDTH, heightInItem);
        caseRender.setCaseTape(dropController.getCaseTape());


        int windowWidth = itemRenderSide * COUNT_ITEM_IN_WIDTH;
        int windowHeight = itemRenderSide * heightInItem;
        createWindow(windowWidth, windowHeight);

        loadResource();

        testRandom(testCaseIteration);
    }

    private void createWindow(int width, int height) throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(width, height));
        Display.setFullscreen(false);
        Display.setResizable(false);
        Display.setTitle("CaseSimulator");

        Display.create();
        Keyboard.create();
        Mouse.create();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glViewport(0, 0, width, height);

//        GL11.glEnable(GL11.GL);
        GL11.glClearColor(1, 1, 1, 1);
    }

    private void loadResource() {
        caseRender.loadResource();

        caseStorage.getItemDropList().forEach((ItemDrop itemDrop) -> {
            Texture tex = Helper.loadTexture(itemDrop.getTexturePath());
            itemDrop.setTexture(tex);
        });
    }

    private void testRandom(int caseIteration) {
        Map<ItemDrop, Integer> items = new HashMap<>(64);
        for (ItemDrop idr : caseStorage.getItemDropList()) {
            items.put(idr, 0);
        }

        for (int i = 0; i < caseIteration; i++) {
            ItemDrop itemDrop = caseStorage.getRandomItem();
            items.put(itemDrop, items.get(itemDrop) + 1);
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Test random for " + caseIteration + " items.").append("\n");
        builder.append("Start test.").append("\n");
        for (Map.Entry<ItemDrop, Integer> entry : items.entrySet()) {
            String str = entry.getKey().getName() + " - current/real percent drop: (" + (entry.getValue() * 100.0F / caseIteration) + ", " + (caseStorage.getItemDropRate(entry.getKey())) + ")";
            builder.append(str).append("\n");
        }
        builder.append("End test.");

        try {
            Path path = Paths.get("RandomTest.txt");
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException e) {}

            Files.write(path, Collections.singleton(builder));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(builder.toString());
    }

    private void update() {
        dropController.update();

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            stop();
        }
    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        caseRender.render();
    }

    private void destroy() {
        caseRender.destroy();
        caseStorage.destroyItems();

        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }


    public static ConfigManager getConfigManager() {
        if (configManager == null) {
            configManager = new ConfigManager();
        }

        return configManager;
    }

    public static CaseStorage getCaseStorage() {
        return caseStorage;
    }

    public static boolean isIsAlive() {
        return isAlive;
    }

    public static int getTick() {
        return tick;
    }

    public static void stop() {
        isAlive = false;
    }

}
