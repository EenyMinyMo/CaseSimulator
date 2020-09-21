package ru.somber.profiler;

import java.util.Objects;

public class ProfilerCell {
    private String nameCell;
    private long startTime;
    private long endTime;

    public ProfilerCell(String nameCell) {
        this.nameCell = nameCell;
        startTime = -1;
        endTime = -1;
    }

    public String getNameCell() {
        return nameCell;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfilerCell that = (ProfilerCell) o;
        return nameCell.equals(that.nameCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameCell);
    }

    @Override
    public String toString() {
        return "ProfilerCell{" +
                "nameCell='" + nameCell + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
