package com.esaygo.app.rx.event;


/**
 *
 * 描述:事件
 */
public class Event {
    public static class RegionEntrancePositionEvent {
        public int position;
    }

    public static class ExitEvent {
        public int exit;
    }

    public static class AllStationPositionEvent {
        public int position;
    }

    public static class StartNavigationEvent {
        public boolean start;
    }


}
