// IEventHandler.aidl
package com.fadryl.media.eventshuttlemp;

// Declare any non-default types here with import statements

interface IEventHandler {
    void handleEvent(in @nullable String name, in @nullable Bundle bundle);
}