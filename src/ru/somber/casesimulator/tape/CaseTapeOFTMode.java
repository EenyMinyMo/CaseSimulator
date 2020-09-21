package ru.somber.casesimulator.tape;

import ru.somber.casesimulator.CaseRun;
import ru.somber.casesimulator.CaseStorage;
import ru.somber.casesimulator.ItemDrop;
import ru.somber.casesimulator.util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CaseTapeOFTMode implements CaseTape {
    private CaseStorage caseStorage;

    /** Визуальная лента вещей */
    private List<ItemDrop> itemTape;

    /** Стандартный шаг смещения позиции в ленте. */
    private final float speedTape = 3.0F / CaseRun.FPS;
    /** Накопительная переменная для плавного падения скорости ленты при остановке кейса. */
    private float cooldown;
    /** Текущая позиция в лента вещей. */
    private float position;
    /** Величина затухания скорости ленты. */
    private final float cooldownStep = 0.18F / CaseRun.FPS;

    /** Количество ячеек визуального дропа в ленте. */
    private final int countVisualDrop = 100;

    /** Флаг, true - лента остановлена, false - лента еще в движении. */
    private boolean isStop;
    /** Флаг, true - лента в данный момент останавливается или уже остановилась, false - лента движется в штатном режиме. */
    private boolean isStopping;


    public CaseTapeOFTMode(CaseStorage caseStorage) {
        this.caseStorage = caseStorage;

        itemTape = new ArrayList<>(countVisualDrop * 4 / 3);
    }

    @Override
    public void updateTape() {
        if (isStop) {
            return;
        }

        float offset = speedTape - speedTape * (cooldown);

        if (isStopping) {
            cooldown += cooldownStep;
        }

        if (offset < Helper.EPS4) {
            isStop = true;
        } else {
            position += offset;
            position %= countVisualDrop;
        }
    }

    @Override
    public void stopTape(ItemDrop itemDrop) {
        isStopping = true;

        if (itemDrop == null) {
            position += distanceToStop();
            position %= countVisualDrop;
            cooldown = 100000;

            return;
        }

        float distance = distanceToStop();
        float dropItemPosition = position + distance;
        dropItemPosition %= countVisualDrop;

        itemTape.set(((int) dropItemPosition), itemDrop);
    }

    @Override
    public void reset() {
        isStop = false;
        isStopping = false;
        cooldown = 0;

        generateItemTape();
    }

    @Override
    public List<ItemDrop> getItemDropList() {
        return new ArrayList<>(itemTape);
    }

    @Override
    public boolean isStop() {
        return isStop;
    }


    public ItemDrop getItemDrop(float position) {
        position %= countVisualDrop;
        return itemTape.get((int) position);
    }

    public ItemDrop[] getItemDropAroundPosition(float position, int radius) {
        int countItemDrop = radius * 2 + 1;
        ItemDrop[] drops = new ItemDrop[countItemDrop];

        position = (int) position;
        position -= radius;
        while (position < 0) {
            position += countVisualDrop;
        }
        position %= countVisualDrop; //Необязательно ?

        for (int i = 0; i < countItemDrop; i++) {
            drops[i] = itemTape.get((int) position);
            position++;
            position %= countVisualDrop;
        }

        return drops;
    }

    public float getSpeedTape() {
        return speedTape;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getPosition() {
        return position;
    }

    public float getCooldownStep() {
        return cooldownStep;
    }

    public int getCountVisualDrop() {
        return countVisualDrop;
    }

    public boolean isStopping() {
        return isStopping;
    }


    private void generateItemTape() {
        List<ItemDrop> itemDropList = caseStorage.getItemDropList();
        Random rand = new Random();

        itemTape.clear();
        for (int i = 0; i < countVisualDrop; i++) {
            int index = rand.nextInt(itemDropList.size());
            itemTape.add(itemDropList.get(index));
        }
    }

    private float distanceToStop() {
        float distance = 0;
        float copyCooldown = cooldown;
        float offset = speedTape - speedTape * (copyCooldown);

        while (offset >= Helper.EPS4) {
            distance += offset;

            copyCooldown += cooldownStep;
            offset = speedTape - speedTape * (copyCooldown);
        }
        return distance;
    }

}
