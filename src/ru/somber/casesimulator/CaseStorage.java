package ru.somber.casesimulator;

import ru.somber.casesimulator.util.Helper;

import java.util.*;

public class CaseStorage {
    private List<ItemDrop> itemDropList;
    private Random random;


    public CaseStorage() {
        itemDropList = new ArrayList<>();
    }

    public void addItemDrop(ItemDrop itemDrop) {
        itemDropList.add(itemDrop);
    }

    public void addItemDrops(ItemDrop[] itemDrop) {
        itemDropList.addAll(Arrays.asList(itemDrop));
    }

    public void removeItemDrop(ItemDrop itemDrop) {
        itemDropList.remove(itemDrop);
    }

    public float getItemDropRate(ItemDrop itemDrop) {
        for (ItemDrop iadr : itemDropList) {
            if (iadr.equals(itemDrop)) {
                return (iadr.getDropNumberValue() / getSumDropRateItems()) * 100;
            }
        }

        return 0;
    }

    public float getSumDropRateItems() {
        float sumDropRate = 0;
        for (ItemDrop iadr : itemDropList) {
            sumDropRate += iadr.getDropNumberValue();
        }
        return sumDropRate;
    }

    public List<ItemDrop> getItemDropList() {
        return new ArrayList<>(itemDropList);
    }

    public int countItemDrop() {
        return itemDropList.size();
    }

    public List<ItemDrop> getSortByPercentItemDrop() {
        List<ItemDrop> sortItems = getItemDropList();

        sortItems.sort((ItemDrop i1, ItemDrop i2) -> {
            float percent1 = getItemDropRate(i1);
            float percent2 = getItemDropRate(i2);

            int compare;

            if ((percent1 - percent2) > Helper.EPS4) {
                compare = 1;
            } else if ((percent2 - percent1) > Helper.EPS4) {
                compare = -1;
            } else {
                compare = 0;
            }

            if (compare == 0) {
                return i1.getName().compareTo(i2.getName());
            } else {
                return compare;
            }
        });

        return sortItems;
    }

    public void destroyItems() {
        for (ItemDrop id : itemDropList) {
            id.destroy();
        }
    }


    public void setKeyRandom(long keyRandom) {
        this.random = new Random(keyRandom);
    }

    public ItemDrop getRandomItem() {
        ItemDrop itemDrop = getItemDropList().get(getItemDropList().size() - 1);

        List<ItemDrop> itemDropList = getItemDropList();

        float randomItem = random.nextFloat() * getSumDropRateItems();
        for (ItemDrop id : itemDropList) {
            randomItem -= id.getDropNumberValue();
            if (randomItem <= Helper.EPS4) {
                itemDrop = id;
                break;
            }
        }

        return itemDrop;
    }

}
