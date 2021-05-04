package ncku.pd2finalapp.ui.map;

class MapState {
    private static final short MAP_RECEIVED = 1;
    private static final short MAP_VIEW_RENDERED = 2;
    private static final short MAP_VIEW_READY = 3;
    private short mapState = 0;
    private Block onMapViewReady = null;

    MapState onMapViewReady(Block onReady) {
        onMapViewReady = onReady;
        return this;
    }

    void setMapReady() {
        if ((mapState & MAP_RECEIVED) != 0) {
            return;
        }
        mapState |= MAP_RECEIVED;
        if (mapState == MAP_VIEW_READY) {
            if (onMapViewReady != null) {
                onMapViewReady.execute();
            }
        }
    }

    void setViewRendered() {
        if ((mapState & MAP_VIEW_RENDERED) != 0) {
            return;
        }
        mapState |= MAP_VIEW_RENDERED;
        if (mapState == MAP_VIEW_READY) {
            if (onMapViewReady != null) {
                onMapViewReady.execute();
            }
        }
    }
}
