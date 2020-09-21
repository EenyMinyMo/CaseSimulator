package ru.somber.profiler;

import java.util.*;

public class ProfilerManager {
    private static ProfilerManager defaultInstance;


    private Set<ProfilerCell> cells;


    public ProfilerManager() {
        cells = new HashSet<>();
    }

    /**
     * Создает новую запоминающую ячейку с заданным именем.
     */
    public void newCell(String nameCell) {
        if (nameCell == null)
            throw new RuntimeException("Null name");

        if (cells.stream().anyMatch((cell) -> cell.getNameCell().equals(nameCell) ))
            throw new RuntimeException("A cell named " + nameCell + " has already been created.");

        ProfilerCell newCell = new ProfilerCell(nameCell);
        newCell.setStartTime(-1);
        newCell.setEndTime(-1);
        cells.add(newCell);
    }

    /**
     * Сохраняет текущее время в наносекундах для ячейки с заданным именем как начальное.
     */
    public void startTime(String nameCell) {
        Optional<ProfilerCell> optionalCell = cells.stream().filter((cell) -> cell.getNameCell().equals(nameCell)).findFirst();

        if (! optionalCell.isPresent())
            throw new RuntimeException("Cell named " + nameCell + " not created.");

        optionalCell.get().setStartTime(System.nanoTime());
        optionalCell.get().setEndTime(-1);
    }

    /**
     * Сохраняет текущее время в наносекундах для ячейки с заданным именем как конечное.
     */
    public void endTime(String nameCell) {
        Optional<ProfilerCell> optionalCell = cells.stream().filter((cell) -> cell.getNameCell().equals(nameCell)).findFirst();

        if (! optionalCell.isPresent())
            throw new RuntimeException("Cell named " + nameCell + " not created.");

        if (optionalCell.get().getStartTime() == -1)
            throw new RuntimeException("Time for " + nameCell + " cell was not running.");

        optionalCell.get().setEndTime(System.nanoTime());
    }

    /**
     * Находит разницу во времени между начальным и конечным значениями времени для ячейки с заданным именем.
     * Перед вызовом этого метода у ячейки с переданным именем должны быть обязательно вызваны
     * методы {@code startTime()} и {@code endTime()}!
     *
     * @return разница во времени между начальным и конечным значениями времени для ячейки с заданным именем.
     */
    public long getProfilerCellTime(String nameCell) {
        Optional<ProfilerCell> optionalCell = cells.stream().filter((cell) -> cell.getNameCell().equals(nameCell)).findFirst();

        if (! optionalCell.isPresent())
            throw new RuntimeException("Cell named " + nameCell + " not created.");

        ProfilerCell cell = optionalCell.get();
        if (cell.getEndTime() == -1)
            throw new RuntimeException("The collection time has not been stopped.");

        return cell.getEndTime() - cell.getStartTime();
    }

    /**
     * Возвращает отображение всех имен ячеек и разницой времени между вызовами методов {@code startTime()} и {@code endTime()}.
     * Если для какой-либо ячейки методы записи времени не вызывались, данные этой ячейки не попадут в отображение.
     */
    public Map<String, Long> getAllProfilerCellTimes() {
        Map<String, Long> profs = new HashMap<>(cells.size());

        cells.forEach((cell) -> {
            if (cell.getEndTime() != -1) {
                String name = cell.getNameCell();
                long deltaTime = cell.getEndTime() - cell.getStartTime();

                profs.put(name, deltaTime);
            }
        });

        return profs;
    }

    /**
     * Обнуляет временные данные всех ячеек.
     */
    public void zeroingAllCells() {
        cells.forEach((cell) -> {
            cell.setStartTime(-1);
            cell.setEndTime(-1);
        });
    }

    /**
     * Удаляет ячейку с заданным временем.
     */
    public void deleteCell(String nameCell) {
        cells.removeIf((cell) -> cell.getNameCell().equals(nameCell));
    }

    /**
     * Удаляет все хранимые ячейки.
     */
    public void deleteAllCells() {
        cells.clear();
    }

    @Override
    public String toString() {
        return "ProfilerManager{" +
                "cells=" + cells +
                '}';
    }

    public static ProfilerManager getDefaultInstance() {
        if (defaultInstance == null)
            defaultInstance = new ProfilerManager();

        return defaultInstance;
    }
}
