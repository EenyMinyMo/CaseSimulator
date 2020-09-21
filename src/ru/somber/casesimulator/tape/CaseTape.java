package ru.somber.casesimulator.tape;

import ru.somber.casesimulator.ItemDrop;

import java.util.List;

public interface CaseTape {

    void updateTape();

    void stopTape(ItemDrop itemDrop);

    void reset();

    List<ItemDrop> getItemDropList();

    boolean isStop();

    boolean isStopping();

}
