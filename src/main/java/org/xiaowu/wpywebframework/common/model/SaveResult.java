package org.xiaowu.wpywebframework.common.model;

import lombok.Generated;

public class SaveResult {
    private int added;
    private int updated;

    public int getTotal() {
        return this.added + this.updated;
    }

    public SaveResult merge(SaveResult result) {
        SaveResult res = of(this.added, this.updated);
        res.added += result.getAdded();
        res.updated += result.getUpdated();
        return res;
    }

    public String toString() {
        int var10000 = this.added;
        return "added " + var10000 + ",updated " + this.updated + ",total " + this.getTotal();
    }

    @Generated
    public int getAdded() {
        return this.added;
    }

    @Generated
    public int getUpdated() {
        return this.updated;
    }

    @Generated
    public void setAdded(final int added) {
        this.added = added;
    }

    @Generated
    public void setUpdated(final int updated) {
        this.updated = updated;
    }

    @Generated
    private SaveResult(final int added, final int updated) {
        this.added = added;
        this.updated = updated;
    }

    @Generated
    public static SaveResult of(final int added, final int updated) {
        return new SaveResult(added, updated);
    }
}
