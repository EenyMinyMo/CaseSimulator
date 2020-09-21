package ru.somber.casesimulator.menu;

import ru.somber.casesimulator.CaseRun;
import ru.somber.casesimulator.Start;
import ru.somber.casesimulator.util.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class StartMenuWindow {
    private JFrame frame;

    private CaseSettingsContainer csc;


    public StartMenuWindow() {
        csc = new CaseSettingsContainer();
    }

    public void initFrame() {
        frame = new JFrame();
        frame.setTitle("CaseSimulatorMenu");
        frame.setSize(new Dimension(300, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        crateSettingTabbedPane();
    }

    public void setVisible() {
        frame.setVisible(true);
    }

    public void setInvisible() {
        frame.setVisible(false);
    }

    public void destroyWindow() {
        frame.dispose();
    }

    private void crateSettingTabbedPane() {
        frame.add(createCaseSettingsPanel());
    }

    private JPanel createCaseSettingsPanel() {
        ConfigManager configManager = CaseRun.getConfigManager();
        Vector<String> caseNames = new Vector<>(configManager.getCaseNames());
        String[] renderMods = {CaseMode.OFT.toString(), CaseMode.PRT.toString()};

        csc.setCaseName(caseNames.firstElement());
        csc.setCaseMode(CaseMode.valueOf(renderMods[0]));
        int currentTestIterationValue = 1_000;
        csc.setCountTestIteration(currentTestIterationValue);
        String rk = String.valueOf(System.currentTimeMillis());
        csc.setKeyRandom(rk);

        JComboBox<String> chooseCaseNameComboBox = new JComboBox<>(caseNames);
        chooseCaseNameComboBox.setPreferredSize(new Dimension(200, 30));
        chooseCaseNameComboBox.setSelectedItem(caseNames.firstElement());

        JComboBox<String> chooseCaseMode = new JComboBox<>(renderMods);
        chooseCaseMode.setPreferredSize(new Dimension(200, 30));

        SpinnerModel spinnerModel = new SpinnerNumberModel(currentTestIterationValue, 200, 10_000_000, 200);
        JSpinner testCaseIterationSpinner = new JSpinner(spinnerModel);
        testCaseIterationSpinner.setPreferredSize(new Dimension(200, 30));

        JTextField keyRandomTextField = new JTextField(rk);
        keyRandomTextField.setPreferredSize(new Dimension(200, 30));

        JButton startButton = new JButton("Start simulation");
        startButton.setPreferredSize(new Dimension(200, 30));
        startButton.addActionListener((event) -> {
            try {
                csc.setKeyRandom(keyRandomTextField.getText());
                csc.setCaseName((String) chooseCaseNameComboBox.getSelectedItem());
                csc.setCountTestIteration((int) testCaseIterationSpinner.getValue());
                csc.setCaseMode(CaseMode.valueOf((String) chooseCaseMode.getSelectedItem()));

                Start.getCaseRun().setCaseSettings(csc);
                destroyWindow();
                Start.getCaseRun().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        JPanel chooseCaseNamePanel = new JPanel();
        chooseCaseNamePanel.setBorder(BorderFactory.createTitledBorder("Choose case name"));
        chooseCaseNamePanel.setPreferredSize(new Dimension(230, 65));
        chooseCaseNamePanel.add(chooseCaseNameComboBox);

        JPanel chooseCaseModePanel = new JPanel();
        chooseCaseModePanel.setBorder(BorderFactory.createTitledBorder("Choose case render mode"));
        chooseCaseModePanel.setPreferredSize(new Dimension(230, 65));
        chooseCaseModePanel.add(chooseCaseMode);

        JPanel testCaseIterationPanel = new JPanel();
        testCaseIterationPanel.setBorder(BorderFactory.createTitledBorder("Count test iteration"));
        testCaseIterationPanel.setPreferredSize(new Dimension(230, 65));
        testCaseIterationPanel.add(testCaseIterationSpinner);

        JPanel keyRandomTextPanel = new JPanel();
        keyRandomTextPanel.setBorder(BorderFactory.createTitledBorder("Random key"));
        keyRandomTextPanel.setPreferredSize(new Dimension(230, 65));
        keyRandomTextPanel.add(keyRandomTextField);

        JPanel startButtonPanel = new JPanel();
        startButtonPanel.setBorder(BorderFactory.createTitledBorder("Start!"));
        startButtonPanel.setPreferredSize(new Dimension(230, 65));
        startButtonPanel.add(startButton);

        JPanel settingsCasePanel = new JPanel();
        settingsCasePanel.add(chooseCaseNamePanel);
        settingsCasePanel.add(chooseCaseModePanel);
        settingsCasePanel.add(testCaseIterationPanel);
        settingsCasePanel.add(keyRandomTextPanel);
        settingsCasePanel.add(startButtonPanel);

        return settingsCasePanel;
    }


    //Взято с http://java-online.ru/swing-layout.xhtml
    private static class CustomLayout implements LayoutManager {
        private Dimension size = new Dimension();

        // Следующие два метода не используются
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        // Метод определения минимального размера для контейнера
        public Dimension minimumLayoutSize(Container c) {
            return calculateBestSize(c);
        }

        // Метод определения предпочтительного размера для контейнера
        public Dimension preferredLayoutSize(Container c) {
            return calculateBestSize(c);
        }

        // Метод расположения компонентов в контейнере
        public void layoutContainer(Container container) {
            // Список компонентов
            Component list[] = container.getComponents();
            int currentY = 5;
            for (int i = 0; i < list.length; i++) {
                // Определение предпочтительного размера компонента
                Dimension pref = list[i].getPreferredSize();
                // Размещение компонента на экране
                list[i].setBounds(5, currentY, pref.width, pref.height);
                // Учитываем промежуток в 5 пикселов
                currentY += 5;
                // Смещаем вертикальную позицию компонента
                currentY += pref.height;
            }
        }

        // Метод вычисления оптимального размера контейнера
        private Dimension calculateBestSize(Container c) {
            // Вычисление длины контейнера
            Component[] list = c.getComponents();
            int maxWidth = 0;
            for (int i = 0; i < list.length; i++) {
                int width = list[i].getWidth();
                // Поиск компонента с максимальной длиной
                if ( width > maxWidth )
                    maxWidth = width;
            }
            // Размер контейнера в длину с учетом левого отступа
            size.width = maxWidth + 5;
            // Вычисление высоты контейнера
            int height = 0;
            for (int i = 0; i < list.length; i++) {
                height += 5;
                height += list[i].getHeight();
            }
            size.height = height;
            return size;
        }
    }


}
