package com.wildboar.vaadin.addon.googlemap.server;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;
import com.wildboar.vaadin.addon.googlemap.client.VGoogleMap;
import com.wildboar.vaadin.addon.googlemap.overlay.BasicMarkerSource;
import com.wildboar.vaadin.addon.googlemap.overlay.InfoWindowTab;
import com.wildboar.vaadin.addon.googlemap.overlay.Marker;
import com.wildboar.vaadin.addon.googlemap.overlay.MarkerSource;
import com.wildboar.vaadin.addon.googlemap.overlay.PolyOverlay;
import com.wildboar.vaadin.addon.googlemap.overlay.Polygon;

@ClientWidget(VGoogleMap.class)
public class GoogleMap extends AbstractComponent {
    private static final long serialVersionUID = -7237970245561106947L;

    public enum MapControl {
        SmallMapControl, HierarchicalMapTypeControl, LargeMapControl, MapTypeControl, MenuMapTypeControl, OverviewMapControl, ScaleControl, SmallZoomControl
    }

    private Point2D.Double center;
    private int zoom;

    private boolean boundsChanged = false;
    private Point2D.Double boundsNE;
    private Point2D.Double boundsSW;

    private final List<MapMoveListener> moveListeners = new ArrayList<MapMoveListener>();
    private final List<MapClickListener> mapClickListeners = new ArrayList<MapClickListener>();
    private final List<MarkerClickListener> markerListeners = new ArrayList<MarkerClickListener>();
    private final List<MarkerMovedListener> markerMovedListeners = new ArrayList<MarkerMovedListener>();

    private MarkerSource markerSource = null;

    private Marker clickedMarker = null;

    private boolean closeInfoWindow = false;

    private final Map<Long, PolyOverlay> overlays = new HashMap<Long, PolyOverlay>();

    private boolean scrollWheelZoomEnabled = true;

    private boolean clearMapTypes = false;

    private final List<MapControl> controls = new ArrayList<MapControl>();

    private final List<CustomMapType> mapTypes = new ArrayList<CustomMapType>();

    private boolean mapTypesChanged = false;

    private boolean reportMapBounds = false;

    private int overviewControl = Constant.CONTROL_DEFAULT;
    private int panControl = Constant.CONTROL_DEFAULT;
    private int rotateControl = Constant.CONTROL_DEFAULT;
    private int scaleControl = Constant.CONTROL_DEFAULT;
    private int streetviewControl = Constant.CONTROL_DEFAULT;
    private int zoomControl = Constant.CONTROL_DEFAULT;

    private final ApplicationResource markerResource = new ApplicationResource() {
        private static final long serialVersionUID = -6926454922185543547L;

        @Override
        public Application getApplication() {
            return GoogleMap.this.getApplication();
        }

        @Override
        public int getBufferSize() {
            return markerSource.getMarkerJSON().length;
        }

        @Override
        public long getCacheTime() {
            return -1;
        }

        @Override
        public String getFilename() {
            return "markersource.txt";
        }

        @Override
        public DownloadStream getStream() {
            return new DownloadStream(new ByteArrayInputStream(markerSource.getMarkerJSON()), getMIMEType(), getFilename());
        }

        @Override
        public String getMIMEType() {
            return "text/plain";
        }
    };

    private String apiKey = "";

    private int clientLogLevel = 0;

    /**
     * Construct a new instance of the map with given size.
     *
     * @param application
     * @link Application owning this instance.
     */
    public GoogleMap(Application application) {
        this(application, new Point2D.Double(-0.001475, 51.477811), 14);
    }

    public GoogleMap(Application application, Point2D.Double center, int zoom) {
        this(application, "", center, zoom);
    }

    /**
     * Construct a new instance of the map with given parameters.
     *
     * @param application
     * @link Application owning this instance.
     * @param center
     *            center of the map as a {@link Point2D.Double}
     * @param zoom
     *            initial zoom level of the map
     */
    public GoogleMap(Application application, String apiKey, Point2D.Double center, int zoom) {
        application.addResource(markerResource);

        this.apiKey = apiKey;
        this.center = center;
        this.zoom = zoom;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        /* MISC */
        target.addAttribute(VGoogleMap.ATTR_API_KEY, apiKey);
        target.addAttribute(VGoogleMap.ATTR_LOG_LEVEL, clientLogLevel);
        target.addAttribute(VGoogleMap.ATTR_SCROLL_WHEEL_ZOOM, scrollWheelZoomEnabled);

        target.addAttribute(VGoogleMap.ATTR_OVERVIEW_MAP_CONTROL, overviewControl);
        target.addAttribute(VGoogleMap.ATTR_PAN_CONTROL, panControl);
        target.addAttribute(VGoogleMap.ATTR_ROTATE_CONTROL, rotateControl);
        target.addAttribute(VGoogleMap.ATTR_SCALE_CONTROL, scaleControl);
        target.addAttribute(VGoogleMap.ATTR_STREET_VIEW_CONTROL, streetviewControl);
        target.addAttribute(VGoogleMap.ATTR_ZOOM_CONTROL, zoomControl);

        if (reportMapBounds) {
            target.addAttribute(VGoogleMap.ATTR_REPORT_BOUNDS, true);
            reportMapBounds = false;
        }

        /* MAP CONTROL */
        for (MapControl control : controls) {
            target.addAttribute(control.name(), true);
        }

        /* MARKER + INFOWINDOW */
        // TODO this feels like a kludge, but unsure how to implement correctly
        if (clickedMarker != null) {
            target.addAttribute(VGoogleMap.ATTR_INFOWINDOW_MARKER, clickedMarker.getId().toString());

            InfoWindowTab[] tabs = clickedMarker.getInfoWindowContent();
            if (clickedMarker.getInfoWindowContent() != null) {
                target.startTag(VGoogleMap.TAG_TABS);
                for (int i = 0; i < tabs.length; i++) {
                    target.startTag(VGoogleMap.TAG_TAB);
                    if (tabs.length > 1) {
                        target.addAttribute(VGoogleMap.ATTR_TAB_SELECTED, tabs[i].isSelected());
                        target.addAttribute(VGoogleMap.ATTR_TAB_LABEL, tabs[i].getLabel());
                    }
                    tabs[i].getContent().paint(target);

                    target.endTag(VGoogleMap.TAG_TAB);
                }
                target.endTag(VGoogleMap.TAG_TABS);
            }

            clickedMarker = null;
        } else if (markerSource != null) {
            target.addAttribute(VGoogleMap.ATTR_MARKER_RESOURCE_FILE, markerResource);
        }

        if (closeInfoWindow) {
            target.addAttribute(VGoogleMap.ATTR_INFOWINDOW_CLOSE, true);
            closeInfoWindow = false;
        }

        /* OVERLAYS */
        if (!overlays.isEmpty()) {
            target.startTag(VGoogleMap.TAG_OVERLAYS);
            for (PolyOverlay poly : overlays.values()) {
                target.startTag(VGoogleMap.TAG_OVERLAY);
                target.addAttribute(VGoogleMap.ATTR_OVERLAY_ID, poly.getId());

                // Encode polyline points as a string attribute
                StringBuilder sb = new StringBuilder();
                Point2D.Double[] points = poly.getPoints();
                for (int i = 0; i < points.length; i++) {
                    if (i > 0) {
                        sb.append(" ");
                    }
                    if (points[i] != null)
                        sb.append("" + points[i].y + "," + points[i].x);
                }
                target.addAttribute(VGoogleMap.ATTR_OVERLAY_POINTS, sb.toString());

                target.addAttribute(VGoogleMap.ATTR_OVERLAY_COLOR, poly.getColor());
                target.addAttribute(VGoogleMap.ATTR_OVERLAY_WEIGHT, poly.getWeight());
                target.addAttribute(VGoogleMap.ATTR_OVERLAY_OPACITY, poly.getOpacity());
                target.addAttribute(VGoogleMap.ATTR_OVERLAY_CLICKABLE, poly.isClickable());

                if (poly instanceof Polygon) {
                    Polygon polygon = (Polygon) poly;
                    target.addAttribute(VGoogleMap.ATTR_OVERLAY_FILLCOLOR, polygon.getFillColor());
                    target.addAttribute(VGoogleMap.ATTR_OVERLAY_FILLOPACITY, polygon.getFillOpacity());
                }
                target.endTag(VGoogleMap.TAG_OVERLAY);
            }
            target.endTag(VGoogleMap.TAG_OVERLAYS);
        }

        /* MAP TYPE */
        if (clearMapTypes) {
            target.addAttribute(VGoogleMap.ATTR_CLEAR_MAP_TYPES, true);
            clearMapTypes = false;
        }

        if (mapTypesChanged) {
            target.startTag(VGoogleMap.TAG_MAP_TYPES);

            for (CustomMapType mapType : mapTypes) {
                mapType.paintContent(target);
            }

            target.endTag(VGoogleMap.TAG_MAP_TYPES);

            mapTypesChanged = false;
        }

        /* VARIABLE */
        if (!boundsChanged) {
            target.addVariable(this, VGoogleMap.VAR_CENTER, latLngPointToStr(center));
            target.addVariable(this, VGoogleMap.VAR_ZOOM, zoom);
        } else if (boundsChanged) {
            target.addVariable(this, VGoogleMap.VAR_BOUNDS_NE, latLngPointToStr(boundsNE));
            target.addVariable(this, VGoogleMap.VAR_BOUNDS_SW, latLngPointToStr(boundsSW));
            boundsChanged = false;
        }
    }

    /**
     * Receive and handle events and other variable changes from the client.
     *
     * {@inheritDoc}
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey(VGoogleMap.VAR_CLICK_POSITION)) {
            fireClickEvent(variables.get(VGoogleMap.VAR_CLICK_POSITION));
            requestRepaint();
        }

        boolean moveEvent = false;
        Integer intVar;
        if ((intVar = (Integer) variables.get(VGoogleMap.VAR_ZOOM)) != null) {
            zoom = intVar;
            moveEvent = true;
        }

        String stringVar;
        if ((stringVar = (String) variables.get(VGoogleMap.VAR_CENTER)) != null && !stringVar.trim().equals("")) {
            center = strToLatLngPoint(stringVar);
            moveEvent = true;
        }

        if ((stringVar = (String) variables.get(VGoogleMap.VAR_BOUNDS_NE)) != null && !stringVar.trim().equals("")) {
            boundsNE = strToLatLngPoint(stringVar);
            moveEvent = true;
        }

        if ((stringVar = (String) variables.get(VGoogleMap.VAR_BOUNDS_SW)) != null && !stringVar.trim().equals("")) {
            boundsSW = strToLatLngPoint(stringVar);
            moveEvent = true;
        }

        if (moveEvent) {
            fireMoveEvent();
        }

        if (variables.containsKey(VGoogleMap.VAR_MARKER_CLICKED)) {
            clickedMarker = markerSource.getMarker(variables.get(VGoogleMap.VAR_MARKER_CLICKED).toString());
            if (clickedMarker != null) {
                fireMarkerClickedEvent(clickedMarker);
                if (clickedMarker.getInfoWindowContent() != null) {
                    requestRepaint();
                }
            }
        }

        if (variables.containsKey(VGoogleMap.VAR_MARKER_MOVED_ID)) {
            String markerID = variables.get(VGoogleMap.VAR_MARKER_MOVED_ID).toString().replaceAll("\"", "");
            List<Marker> markers = markerSource.getMarkers();
            //System.out.println("markerMovedId : " + markerID);
            for (Marker mark : markers) {
                //System.out.println("check : " + mark.getId() + "  " + markerID);
                if (mark.getId() == Long.parseLong(markerID)) {
                    //System.out.println("updating marker position : " + mark.getId());
                    double lat = new Double(variables.get(VGoogleMap.VAR_MARKER_MOVED_LAT).toString());
                    double lng = new Double(variables.get(VGoogleMap.VAR_MARKER_MOVED_LNG).toString());
                    mark.getLatLng().setLocation(lng, lat);

                    fireMarkerMovedEvent(mark);
                    break;
                }
            }
        }
    }

    private void fireMoveEvent() {
        for (MapMoveListener listener : moveListeners) {
            listener.mapMoved(zoom, center, boundsNE, boundsSW);
        }
    }

    private void fireClickEvent(Object object) {
        Point2D.Double clickPos = strToLatLngPoint(object.toString());
        for (MapClickListener listener : mapClickListeners) {
            listener.mapClicked(clickPos);
        }
    }

    private void fireMarkerClickedEvent(Marker clickedMarker) {
        for (MarkerClickListener m : markerListeners) {
            m.markerClicked(clickedMarker);
        }
    }

    private void fireMarkerMovedEvent(Marker movedMarker) {
        for (MarkerMovedListener m : markerMovedListeners) {
            m.markerMoved(movedMarker);
        }
    }

    /**
     * Interface for listening map move and zoom events.
     *
     * @author Henri Muurimaa
     */
    public interface MapMoveListener {
        /**
         * Handle a MapMoveEvent.
         *
         * @param newZoomLevel
         *            New zoom level
         * @param newCenter
         *            New center coordinates
         * @param boundsNE
         *            Coordinates of the north-east corner of the map
         * @param boundsSW
         *            Coordinates of the south-west corner of the map
         */
        public void mapMoved(int newZoomLevel, Point2D.Double newCenter, Point2D.Double boundsNE, Point2D.Double boundsSW);
    }

    /**
     * Interface for listening map click events.
     *
     * @author Henri Muurimaa
     */
    public interface MapClickListener {
        /**
         * Handle a MapClickEvent.
         *
         * @param clickPos
         *            coordinates of the click event.
         *
         */
        public void mapClicked(Point2D.Double clickPos);
    }

    /**
     * Interface for listening marker click events.
     *
     */
    public interface MarkerClickListener {
        /**
         * Handle a MarkerClickEvent.
         *
         * @param clickedMarker
         *            the marker that was clicked.
         *
         */
        public void markerClicked(Marker clickedMarker);
    }

    /**
     * Interface for listening marker move events.
     *
     */
    public interface MarkerMovedListener {
        /**
         * Handle a MarkerMovedEvent.
         *
         * @param movedMarker
         *            the marker that was moved.
         *
         */
        public void markerMoved(Marker movedMarker);
    }

    /**
     * Register a new {@link MapClickListener}.
     *
     * @param listener
     *            new {@link MapClickListener} to register
     */
    public void addListener(MapClickListener listener) {
        if (!mapClickListeners.contains(listener)) {
            mapClickListeners.add(listener);
        }
    }

    /**
     * Deregister a {@link MapClickListener}.
     *
     * @param listener
     *            {@link MapClickListener} to deregister
     */
    public void removeListener(MapClickListener listener) {
        if (mapClickListeners.contains(listener)) {
            mapClickListeners.remove(listener);
        }
    }

    /**
     * Register a new {@link MapMoveListener}.
     *
     * @param listener
     *            new {@link MapMoveListener} to register
     */
    public void addListener(MapMoveListener listener) {
        if (!moveListeners.contains(listener)) {
            moveListeners.add(listener);
        }
    }

    /**
     * Register a new {@link MarkerMovedListener}.
     *
     * NOTE!! The marker that is clicked MUST have some information window
     * content! This is due to the implementation of the Widget, as the marker
     * click events do not propagate if there is not a information window
     * opened.
     *
     * @param listener
     *            new {@link MarkerClickListener} to register
     */
    public void addListener(MarkerClickListener listener) {
        if (!markerListeners.contains(listener)) {
            markerListeners.add(listener);
        }
    }

    /**
     * Register a new {@link MarkerMovedListener}.
     *
     *
     *
     * @param listener
     *            new {@link MarkerMovedListener} to register
     */
    public void addListener(MarkerMovedListener listener) {
        if (!markerMovedListeners.contains(listener)) {
            markerMovedListeners.add(listener);
        }
    }

    /**
     * Deregister a {@link MapMoveListener}.
     *
     * @param listener
     *            {@link MapMoveListener} to deregister
     */
    public void removeListener(MapMoveListener listener) {
        if (moveListeners.contains(listener)) {
            moveListeners.remove(listener);
        }
    }

    /**
     * Deregister a {@link MarkerClickListener}.
     *
     * @param listener
     *            {@link MarkerClickListener} to deregister
     */
    public void removeListener(MarkerClickListener listener) {
        if (markerListeners.contains(listener)) {
            markerListeners.remove(listener);
        }
    }

    /**
     * Deregister a {@link MarkerMovedListener}.
     *
     * @param listener
     *            the {@link MarkerMovedListener} to deregister
     */
    public void removeListener(MarkerMovedListener listener) {
        if (markerMovedListeners.contains(listener)) {
            markerMovedListeners.remove(listener);
        }
    }

    public void setControls(int overview, int pan, int rotate, int scale, int street, int zoom) {
        overviewControl = overview;
        panControl = pan;
        rotateControl = rotate;
        scaleControl = scale;
        streetviewControl = street;
        zoomControl = zoom;
    }

    public int getOverviewControl() {
        return overviewControl;
    }

    public void setOverviewControl(int overviewControl) {
        this.overviewControl = overviewControl;
    }

    public int getPanControl() {
        return panControl;
    }

    public void setPanControl(int panControl) {
        this.panControl = panControl;
    }

    public int getRotateControl() {
        return rotateControl;
    }

    public void setRotateControl(int rotateControl) {
        this.rotateControl = rotateControl;
    }

    public int getScaleControl() {
        return scaleControl;
    }

    public void setScaleControl(int scaleControl) {
        this.scaleControl = scaleControl;
    }

    public int getStreetviewControl() {
        return streetviewControl;
    }

    public void setStreetviewControl(int streetviewControl) {
        this.streetviewControl = streetviewControl;
    }

    public int getZoomControl() {
        return zoomControl;
    }

    public void setZoomControl(int zoomControl) {
        this.zoomControl = zoomControl;
    }

    /**
     * Get current center coordinates of the map.
     *
     * @return
     */
    public Point2D.Double getCenter() {
        return center;
    }

    /**
     * Set the current center coordinates of the map. This method can be used to
     * pan the map programmatically.
     *
     * @param center
     *            the new center coordinates
     */
    public void setCenter(Point2D.Double center) {
        this.center = center;
        requestRepaint();
    }

    /**
     * Get the current zoom level of the map.
     *
     * @return the current zoom level
     */
    public int getZoom() {
        return zoom;
    }

    /**
     * Set the zoom level of the map. This method can be used to zoom the map
     * programmatically.
     *
     * @param zoom
     */
    public void setZoom(int zoom) {
        this.zoom = zoom;
        requestRepaint();
    }

    /**
     * Set the level of verbosity the client side uses for tracing or displaying
     * error messages.
     *
     * @param level
     */
    public void setClientLogLevel(int level) {
        this.clientLogLevel = level;
        requestRepaint();
    }

    /**
     * Get the level of verbosity the client side uses for tracing or displaying
     * error messages.
     */
    public int getClientLogLevel() {
        return clientLogLevel;
    }

    public void setBounds(Point2D.Double ne, Point2D.Double sw) {
        this.boundsNE = ne;
        this.boundsSW = sw;
        this.boundsChanged = true;
        requestRepaint();
    }

    public void showAllMarker() {
        Pair<Point2D.Double, Point2D.Double> bb = markerSource.getBoundingBox();
        if (bb == null)
            return;
        setBounds(bb.getFirst(), bb.getSecond());
    }

    /**
     * Get the coordinates of the north-east corner of the map.
     *
     * @return
     */
    public Point2D.Double getBoundsNE() {
        return boundsNE;
    }

    /**
     * Get the coordinates of the south-west corner of the map.
     *
     * @return
     */
    public Point2D.Double getBoundsSW() {
        return boundsSW;
    }

    /**
     * Set the {@link MarkerSource} for the map.
     *
     * @param markerSource
     */
    public void setMarkerSource(MarkerSource markerSource) {
        this.markerSource = markerSource;
    }

    /**
     * Close the currently open info window, if any.
     */
    public void closeInfoWindow() {
        closeInfoWindow = true;
        requestRepaint();
    }

    /**
     * Add a new {@link PolyOverlay} to the map. Does nothing if the overlay
     * already exist on the map.
     *
     * @param overlay
     *            {@link PolyOverlay} to add
     *
     * @return True if the overlay was added.
     */
    public boolean addPolyOverlay(PolyOverlay overlay) {
        if (!overlays.containsKey(overlay.getId())) {
            overlays.put(overlay.getId(), overlay);
            requestRepaint();
            return true;
        }

        return false;
    }

    /**
     * Update a {@link PolyOverlay} on the map. Does nothing if the overlay does
     * not exist on the map.
     *
     * @param overlay
     *            {@link PolyOverlay} to update
     *
     * @return True if the overlay was updated.
     */
    public boolean updateOverlay(PolyOverlay overlay) {
        if (overlays.containsKey(overlay.getId())) {
            overlays.put(overlay.getId(), overlay);
            requestRepaint();
            return true;
        }

        return false;
    }

    /**
     * Remove a {@link PolyOverlay} from the map. Does nothing if the overlay
     * does not exist on the map.
     *
     * @param overlay
     *            {@link PolyOverlay} to remove
     *
     * @return True if the overlay was removed.
     */
    public boolean removeOverlay(PolyOverlay overlay) {
        if (overlays.containsKey(overlay.getId())) {
            overlays.remove(overlay.getId());
            requestRepaint();
            return true;
        }

        return false;
    }

    public boolean removeOverlay(long overlayId) {
        if (overlays.containsKey(overlayId)) {
            overlays.remove(overlayId);
            requestRepaint();
            return true;
        }

        return false;
    }

    public void removeAllOverlays() {
        overlays.clear();
        requestRepaint();
    }

    /**
     * Get the collection of {@link PolyOverlay}s currently in the map.
     *
     * @return a {@link Collection} of overlays.
     */
    public Collection<PolyOverlay> getOverlays() {
        return overlays.values();
    }



    /**
     * Add a Marker to the current MarkerSource. If the map has no marker source
     * a new {@link BasicMarkerSource} is created.
     *
     * @param marker
     *            Marker to add
     */
    public void addMarker(Marker marker) {
        if (markerSource == null) {
            markerSource = new BasicMarkerSource();
        }

        markerSource.addMarker(marker);
        requestRepaint();
    }

    /**
     * Removes the marker from the map
     *
     * @param marker
     */
    public void removeMarker(Marker marker) {
        if (markerSource != null) {
            markerSource.getMarkers().remove(marker);
            requestRepaint();
        }
    }

    public void removeAllMarkers() {
        if (markerSource != null) {
            markerSource.getMarkers().clear();
            requestRepaint();
        }
    }

    public void setScrollWheelZoomEnabled(boolean isEnabled) {
        scrollWheelZoomEnabled = isEnabled;
    }

    public boolean isScrollWheelZoomEnabled() {
        return scrollWheelZoomEnabled;
    }

    public boolean addControl(MapControl control) {
        if (!controls.contains(control)) {
            controls.add(control);
            return true;
        }

        return false;
    }

    public boolean hasControl(MapControl control) {
        return controls.contains(control);
    }

    public boolean removeControl(MapControl control) {
        if (controls.contains(control)) {
            controls.remove(control);
            return true;
        }

        return false;
    }

    public void addMapType(String name, int minZoom, int maxZoom, String copyright, String tileUrl, boolean isPng, double opacity) {
        mapTypes.add(new CustomMapType(name, minZoom, maxZoom, copyright, tileUrl, isPng, opacity));

        mapTypesChanged = true;

        requestRepaint();
    }

    public void clearMapTypes() {
        mapTypes.clear();
        clearMapTypes = true;
        requestRepaint();
    }

    public void reportMapBounds() {
        reportMapBounds = true;
        requestRepaint();
    }

    class CustomMapType {
        private final double opacity;
        private final String tileUrl;
        private final boolean isPng;
        private final int minZoom;
        private final int maxZoom;
        private final String copyright;
        private final String name;

        public CustomMapType(String name, int minZoom, int maxZoom, String copyright, String tileUrl, boolean isPng, double opacity) {
            this.name = name;
            this.minZoom = minZoom;
            this.maxZoom = maxZoom;
            this.copyright = copyright;
            this.tileUrl = tileUrl;
            this.isPng = isPng;
            this.opacity = opacity;
        }

        public void paintContent(PaintTarget target) throws PaintException {
            target.startTag(VGoogleMap.TAG_MAP_TYPE);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_NAME, name);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_MIN_ZOOM, minZoom);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_MAX_ZOOM, maxZoom);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_COPYRIGHT, copyright);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_TILE_URL, tileUrl);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_IS_PNG, isPng);
            target.addAttribute(VGoogleMap.ATTR_MAPTYPE_OPACITY, opacity);
            target.endTag(VGoogleMap.TAG_MAP_TYPE);
        }

        public double getOpacity() {
            return opacity;
        }

        public String getTileUrl() {
            return tileUrl;
        }

        public boolean isPng() {
            return isPng;
        }

        public int getMinZoom() {
            return minZoom;
        }

        public int getMaxZoom() {
            return maxZoom;
        }

        public String getCopyright() {
            return copyright;
        }
    }

    public static Point2D.Double strToLatLngPoint(String latLngStr) {
        if (latLngStr == null) {
            return null;
        }

        String nums[] = latLngStr.split(", ");
        if (nums.length != 2) {
            return null;
        }

        //double lat = Double.parseDouble(nums[0].substring(1));
        //double lng = Double.parseDouble(nums[1].substring(0, nums[1].length() - 1));

        double lat = Double.parseDouble(nums[0]);
        double lng = Double.parseDouble(nums[1]);

        return new Point2D.Double(lng, lat);
    }

    public static String latLngPointToStr(Point2D.Double latLng) {
        if (latLng == null)
            return null;
        return latLng.y + ", " + latLng.x;
    }

}