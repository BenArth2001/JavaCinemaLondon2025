package model.report;

import java.util.ArrayList;
import java.util.List;

public class TheaterUtilizationReport {
    private List<TheaterUtilizationEntry> entries = new ArrayList<>();

    public List<TheaterUtilizationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TheaterUtilizationEntry> entries) {
        this.entries = entries;
    }
}
