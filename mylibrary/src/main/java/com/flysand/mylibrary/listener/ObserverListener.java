package com.flysand.mylibrary.listener;

import android.content.Intent;

/**
 * Created by FlySand on 2017/11/6.
 */

public interface ObserverListener {
    void onUpdate(int type, Intent intent);
    void sendUpdateMessage(int type, Intent intent);


    interface Subject {
        void registerObserver(ObserverListener o);

        void removeObserver(ObserverListener o);

        void notifyObservers(int type, Intent intent);

    }
}

