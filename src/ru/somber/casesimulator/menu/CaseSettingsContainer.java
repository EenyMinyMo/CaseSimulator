package ru.somber.casesimulator.menu;

import java.util.Objects;

public class CaseSettingsContainer {
    private String caseName;
    private CaseMode caseMode;
    private int countTestIteration;
    private String keyRandom;


    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public CaseMode getCaseMode() {
        return caseMode;
    }

    public void setCaseMode(CaseMode caseMode) {
        this.caseMode = caseMode;
    }

    public int getCountTestIteration() {
        return countTestIteration;
    }

    public void setCountTestIteration(int countTestIteration) {
        this.countTestIteration = countTestIteration;
    }

    public String getKeyRandom() {
        return keyRandom;
    }

    public void setKeyRandom(String keyRandom) {
        this.keyRandom = keyRandom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseSettingsContainer that = (CaseSettingsContainer) o;
        return countTestIteration == that.countTestIteration &&
                Objects.equals(caseName, that.caseName) &&
                caseMode == that.caseMode &&
                Objects.equals(keyRandom, that.keyRandom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseName, caseMode, countTestIteration, keyRandom);
    }

    @Override
    public String toString() {
        return "CaseSettingsContainer{" +
                "caseName='" + caseName + '\'' +
                ", caseMode=" + caseMode +
                ", countTestIteration=" + countTestIteration +
                ", keyRandom='" + keyRandom + '\'' +
                '}';
    }
}
