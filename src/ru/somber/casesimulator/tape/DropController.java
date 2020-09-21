package ru.somber.casesimulator.tape;

import org.lwjgl.input.Keyboard;
import ru.somber.casesimulator.CaseStorage;
import ru.somber.casesimulator.ItemDrop;
import ru.somber.casesimulator.menu.CaseMode;

public class DropController {
    private CaseStorage caseStorage;
    private CaseMode caseMode;

    private CaseTape caseTape;

    public DropController(CaseStorage caseStorage, CaseMode caseMode) {
        this.caseStorage = caseStorage;
        this.caseMode = caseMode;

        if (caseMode == CaseMode.OFT) {
            caseTape = new CaseTapeOFTMode(caseStorage);
            caseTape.reset();
        } else {
            caseTape = new CaseTapePRTMode(caseStorage);
        }
    }

    public void update() {
        caseTape.updateTape();

        while (Keyboard.next()) {
            if ((Keyboard.getEventKey() == Keyboard.KEY_SPACE) && (Keyboard.getEventKeyState())) {
                if (! caseTape.isStop()) {
                    if (! caseTape.isStopping()) {
                        ItemDrop drop = caseStorage.getRandomItem();
                        caseTape.stopTape(drop);

                        System.out.println(drop.getName() + " | " + caseStorage.getItemDropRate(drop));
                    } else {
                        caseTape.stopTape(null);
                    }
                } else {
                    caseTape.reset();
                }
                break;
            }
        }
    }

    public CaseTape getCaseTape() {
        return caseTape;
    }
}
