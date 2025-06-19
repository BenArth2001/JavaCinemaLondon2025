package model.report;

import java.util.ArrayList;
import java.util.List;

public class MoviePopularityReport {
    private List<MoviePopularityEntry> entries = new ArrayList<>();

    public List<MoviePopularityEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<MoviePopularityEntry> entries) {
        this.entries = entries;
    }
}
