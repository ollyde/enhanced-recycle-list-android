package com.polydelic.oliverdixon.ollysenhancedlist.src.ListModels;

import android.support.annotation.Nullable;

public class ListModelTemporary {

    private @Nullable Integer customLayout;

    public @Nullable Integer getCustomLayout() {
        return customLayout;
    }

    /**
     * @param customLayout set to null if you want to use defaults.
     */
    public void setCustomLayout(@Nullable Integer customLayout) {
        this.customLayout = customLayout;
    }
}
