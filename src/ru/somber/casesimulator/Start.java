package ru.somber.casesimulator;

import ru.somber.casesimulator.menu.StartMenuWindow;

public class Start {
    private static StartMenuWindow menuWindow;
    private static CaseRun caseRun;


    private Start() {}


    public static StartMenuWindow getMenuWindow() {
        return menuWindow;
    }

    public static CaseRun getCaseRun() {
        return caseRun;
    }


    private static void run() {
        menuWindow.initFrame();
        menuWindow.setVisible();
    }


    public static void main(String[] args) {
        menuWindow = new StartMenuWindow();
        caseRun = new CaseRun();

        run();
    }

}
