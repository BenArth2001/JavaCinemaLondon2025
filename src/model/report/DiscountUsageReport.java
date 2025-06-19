package model.report;

import java.util.ArrayList;
import java.util.List;

public class DiscountUsageReport {
    private List<DiscountUsageEntry> entries = new ArrayList<>();

    public List<DiscountUsageEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<DiscountUsageEntry> entries) {
        this.entries = entries;
    }
}
