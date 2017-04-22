package com.polydelic.oliverdixon.ollysenhancedlist.src;

import android.view.View;

public interface IRecycleViewOnClickListener {
    enum PressTime { LONG_PRESS, SHORT_PRESS }
    void viewClicked(PressTime pressTime, View viewClicked, BaseViewHolder baseViewHolder);
}
