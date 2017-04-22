package com.polydelic.oliverdixon.ollysenhancedlist.src;

/**
 * If you want anything to be displayed in the list it must implement these methods.
 */
public interface IListModel {
    int getViewLayoutId();
    Class getViewClass();
}
