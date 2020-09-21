package ru.somber.casesimulator.tape;

import ru.somber.casesimulator.CaseStorage;
import ru.somber.casesimulator.ItemDrop;

import java.util.ArrayList;
import java.util.List;

public class CaseTapePRTMode implements CaseTape {
    private CaseStorage caseStorage;

    private ItemDrop drop;
    private boolean isStop;


    public CaseTapePRTMode(CaseStorage caseStorage) {
        this.caseStorage = caseStorage;
        drop = null;
        isStop = false;
    }

    @Override
    public void updateTape() {}

    @Override
    public void stopTape(ItemDrop itemDrop) {
        drop = itemDrop;
        isStop = true;
    }

    @Override
    public void reset() {
        drop = null;
        isStop = false;
    }

    @Override
    public List<ItemDrop> getItemDropList() {
        List<ItemDrop> dropList = new ArrayList<>();
        if (drop != null) {
            dropList.add(drop);
        }

        return dropList;
    }

    @Override
    public boolean isStop() {
        return isStop;
    }

    @Override
    public boolean isStopping() {
        return isStop;
    }
}
