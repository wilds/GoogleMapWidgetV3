package com.wildboar.vaadin.addon.googlemap.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.ajaxloader.client.Properties.TypeException;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.base.Point;
import com.google.gwt.maps.client.base.Size;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.controls.OverviewMapControlOptions;
import com.google.gwt.maps.client.controls.PanControlOptions;
import com.google.gwt.maps.client.controls.RotateControlOptions;
import com.google.gwt.maps.client.controls.ScaleControlOptions;
import com.google.gwt.maps.client.controls.StreetViewControlOptions;
import com.google.gwt.maps.client.controls.ZoomControlOptions;
import com.google.gwt.maps.client.controls.ZoomControlStyle;
import com.google.gwt.maps.client.events.MapEventType;
import com.google.gwt.maps.client.events.MapHandlerRegistration;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.dragend.DragEndMapEvent;
import com.google.gwt.maps.client.events.dragend.DragEndMapHandler;
import com.google.gwt.maps.client.events.idle.IdleMapEvent;
import com.google.gwt.maps.client.events.idle.IdleMapHandler;
import com.google.gwt.maps.client.maptypes.ImageMapType;
import com.google.gwt.maps.client.maptypes.ImageMapTypeOptions;
import com.google.gwt.maps.client.maptypes.TileUrlCallBack;
import com.google.gwt.maps.client.mvc.MVCObject;
import com.google.gwt.maps.client.overlays.Animation;
import com.google.gwt.maps.client.overlays.InfoWindow;
import com.google.gwt.maps.client.overlays.InfoWindowOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerImage;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.overlays.Polygon;
import com.google.gwt.maps.client.overlays.PolygonOptions;
import com.google.gwt.maps.client.overlays.Polyline;
import com.google.gwt.maps.client.overlays.PolylineOptions;
import com.google.gwt.maps.client.services.DirectionsRenderer;
import com.google.gwt.maps.client.services.DirectionsRendererOptions;
import com.google.gwt.maps.client.services.DirectionsRequest;
import com.google.gwt.maps.client.services.DirectionsResult;
import com.google.gwt.maps.client.services.DirectionsResultHandler;
import com.google.gwt.maps.client.services.DirectionsService;
import com.google.gwt.maps.client.services.DirectionsStatus;
import com.google.gwt.maps.client.services.DirectionsWaypoint;
import com.google.gwt.maps.client.services.Distance;
import com.google.gwt.maps.client.services.DistanceMatrixRequest;
import com.google.gwt.maps.client.services.DistanceMatrixRequestHandler;
import com.google.gwt.maps.client.services.DistanceMatrixResponse;
import com.google.gwt.maps.client.services.DistanceMatrixResponseElement;
import com.google.gwt.maps.client.services.DistanceMatrixResponseRow;
import com.google.gwt.maps.client.services.DistanceMatrixService;
import com.google.gwt.maps.client.services.DistanceMatrixStatus;
import com.google.gwt.maps.client.services.Duration;
import com.google.gwt.maps.client.services.TravelMode;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

/**
 * Client side widget which communicates with the server. Messages from the server are shown as HTML and mouse clicks are sent to the server.
 */
public class VGoogleMap extends Composite implements Paintable {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-googlemap";

    /* TAGS */
    public static final String TAG_TABS = "tabs";
    public static final String TAG_TAB = "tab";
    public static final String TAG_OVERLAYS = "overlays";
    public static final String TAG_OVERLAY = "overlay";
    public static final String TAG_MAP_TYPES = "mapTypes";
    public static final String TAG_MAP_TYPE = "maptype";

    /* ATTRIBUTES */
    public static final String ATTR_LOG_LEVEL = "loglevel";
    public static final String ATTR_WIDTH = "width";
    public static final String ATTR_HEIGHT = "height";
    public static final String ATTR_API_KEY = "apikey";
    public static final String ATTR_SCROLL_WHEEL_ZOOM = "swze";
    public static final String ATTR_MARKER_RESOURCE_FILE = "markerRes";
    public static final String ATTR_CLEAR_MAP_TYPES = "clearMapTypes";
    public static final String ATTR_REPORT_BOUNDS = "reportBounds";

    public static final String ATTR_OVERVIEW_MAP_CONTROL = "controlOverview";
    public static final String ATTR_PAN_CONTROL = "controlPan";
    public static final String ATTR_ROTATE_CONTROL = "controlRotate";
    public static final String ATTR_SCALE_CONTROL = "controlScale";
    public static final String ATTR_STREET_VIEW_CONTROL = "controlStreetView";
    public static final String ATTR_ZOOM_CONTROL = "controlZoom";

    public static final String ATTR_TAB_SELECTED = "selected";
    public static final String ATTR_TAB_LABEL = "label";

    public static final String ATTR_OVERLAY_ID = "id";
    public static final String ATTR_OVERLAY_POINTS = "points";
    public static final String ATTR_OVERLAY_COLOR = "color";
    public static final String ATTR_OVERLAY_WEIGHT = "weight";
    public static final String ATTR_OVERLAY_OPACITY = "opacity";
    public static final String ATTR_OVERLAY_CLICKABLE = "clickable";
    public static final String ATTR_OVERLAY_FILLCOLOR = "fillcolor";        // POLYGON
    public static final String ATTR_OVERLAY_FILLOPACITY = "fillopacity";    // POLYGON

    public static final String ATTR_MAPTYPE_NAME = "name";
    public static final String ATTR_MAPTYPE_MIN_ZOOM = "minZoom";
    public static final String ATTR_MAPTYPE_MAX_ZOOM = "maxZoom";
    public static final String ATTR_MAPTYPE_COPYRIGHT = "copyright";
    public static final String ATTR_MAPTYPE_TILE_URL = "tileUrl";
    public static final String ATTR_MAPTYPE_IS_PNG = "isPng";
    public static final String ATTR_MAPTYPE_OPACITY = "opacity";

    public static final String ATTR_INFOWINDOW_MARKER = "marker";
    public static final String ATTR_INFOWINDOW_CLOSE = "closeInfoWindow";

    public static final String ATTR_DIRECTION_ORIGIN = "drection.origin";
    public static final String ATTR_DIRECTION_DESTINATION = "direction.destination";
    public static final String ATTR_DIRECTION_TRAVELMODE = "direction.travel";
    public static final String ATTR_DIRECTION_WPS = "direction.wp";
    public static final String TAG_DIRECTION_WPS = "direction.wps";

    /* VARIABLES */
    public static final String VAR_ZOOM = "zoom";
    public static final String VAR_CENTER = "center";
    public static final String VAR_BOUNDS_NE = "bounds_ne";
    public static final String VAR_BOUNDS_SW = "bounds_sw";
    public static final String VAR_CLICK_POSITION = "click_pos";

    public static final String VAR_MARKER_MOVED_ID = "markerMovedId";
    public static final String VAR_MARKER_MOVED_LAT = "markerMovedLat";
    public static final String VAR_MARKER_MOVED_LNG = "markerMovedLong";
    public static final String VAR_MARKER_CLICKED = "marker";

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    protected ApplicationConnection client;

    private MapWidget map = null;

    private final Map<String, Marker> knownMarkers = new HashMap<String, Marker>();
    private final Map<Integer, MVCObject<?>> knownPolygons = new HashMap<Integer, MVCObject<?>>();
    private final Map<String, InfoWindow> knownInfowindow = new HashMap<String, InfoWindow>();

    private boolean ignoreVariableChanges = true;

    private long markerRequestSentAt;

    //private final List<MapControl> controls = new ArrayList<MapControl>();

    private int logLevel = 0;

    private MapOptions mapOpt;

    private boolean apiloaded = false;
    private LinkedList<UIDL> stack = new LinkedList<UIDL>();
    private ArrayList<LoadLibrary> loadLibraries;
    private final SimplePanel wrapperPanel;

    protected ClickMapHandler clickMapHanler = new ClickMapHandler() {

        @Override
        public void onEvent(ClickMapEvent event) {
            onClick(event);
        }
    };

    protected DragEndMapHandler dragEndMapHandler = new DragEndMapHandler() {

        @Override
        public void onEvent(DragEndMapEvent event) {
            onMoveEnd(event);
        }
    };

    protected IdleMapHandler idleMapHandler = new IdleMapHandler() {

        @Override
        public void onEvent(IdleMapEvent event) {
            if (ignoreVariableChanges) {
                return;
            }

            reportMapBounds();
        }
    };

    class MarkerDragEndHandlers implements DragEndMapHandler {

        protected Marker marker;

        public MarkerDragEndHandlers(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onEvent(DragEndMapEvent event) {
            event.getProperties().set(VAR_MARKER_CLICKED, marker);
            onDragEnd(event);
        }
    };

    private Runnable onLoad = new Runnable() {
        @Override
        public void run() {
            apiloaded = true;
            loadMap();
        }
    };

    /**
     * The constructor should first call super() to initialize the component and then handle any initialization relevant to Vaadin.
     */
    public VGoogleMap() {

        // load all the libs for use in the maps
        loadLibraries = new ArrayList<LoadLibrary>();
        // loadLibraries.add(LoadLibrary.ADSENSE);
        loadLibraries.add(LoadLibrary.DRAWING);
        loadLibraries.add(LoadLibrary.GEOMETRY);
        // loadLibraries.add(LoadLibrary.PANORAMIO);
        // loadLibraries.add(LoadLibrary.PLACES);
        // loadLibraries.add(LoadLibrary.WEATHER);
        loadLibraries.add(LoadLibrary.VISUALIZATION);

        wrapperPanel = new SimplePanel();
        initWidget(wrapperPanel); // All Composites need to call initWidget()
    }

    /**
     * Once the API has been loaded, the MapWidget can be initialized. This
     * method will initialize the MapWidget and place it inside the wrapper from
     * the composite root.
     */
    private void loadMap() {
        mapOpt = MapOptions.newInstance();

        map = new MapWidget(mapOpt);
        wrapperPanel.add(map);

        map.addDragEndHandler(dragEndMapHandler);
        map.addClickHandler(clickMapHanler);
        map.addIdleHandler(idleMapHandler);

        // This method call of the Paintable interface sets the component
        // style name in DOM tree
        setStyleName(CLASSNAME);

        // Update all the uidl requests that have been made
        if (stack != null) {
            for(UIDL uidl : stack) {
                updateFromUIDL(uidl, client);
            }
        }
        stack = null;
    }

    /**
     * Called whenever an update is received from the server
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and let the containing layout manage caption, etc.
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;
        if (!apiloaded) {
            if (uidl.hasAttribute(ATTR_API_KEY)) {
                LoadApi.go(onLoad, loadLibraries, false, "key=" + uidl.getStringAttribute(ATTR_API_KEY));
            } else {
                LoadApi.go(onLoad, loadLibraries, false);
            }
            if (stack == null) {
                VConsole.error("The ArrayList holding UIDL updates was NULL, this should never happen!");
            }
            stack.add(uidl);
            return;
        }

        // Save the client side identifier (paintable id) for the widget
        paintableId = uidl.getId();

        logLevel = uidl.getIntAttribute(ATTR_LOG_LEVEL);

        long start = System.currentTimeMillis();

        // Do not send any variable changes while changing the map
        ignoreVariableChanges = true;

        if (uidl.hasAttribute(ATTR_WIDTH) || uidl.hasAttribute(ATTR_HEIGHT)) {
            MapHandlerRegistration.trigger(map, MapEventType.RESIZE);
        }

        boolean optChanged = false;
        boolean scrollWheelZoomEnabled = uidl.getBooleanAttribute(ATTR_SCROLL_WHEEL_ZOOM);
        if (mapOpt.getScrollWheel() != scrollWheelZoomEnabled) {
            mapOpt.setScrollWheel(scrollWheelZoomEnabled);
            optChanged = true;
        }

        if (uidl.hasAttribute(ATTR_OVERVIEW_MAP_CONTROL)) {
            int val = uidl.getIntAttribute(ATTR_OVERVIEW_MAP_CONTROL);
            if (val == -1) {
                if (mapOpt.getOverviewMapControl()) {
                    mapOpt.setOverviewMapControl(false);
                    optChanged = true;
                }
            } else if (val >= 0) {
                if (mapOpt.getOverviewMapControlOptions() == null || mapOpt.getOverviewMapControlOptions().getOpened() != (val > 0)) {
                    mapOpt.setOverviewMapControl(true);
                    OverviewMapControlOptions overviewMapControlOptions = OverviewMapControlOptions.newInstance();
                    overviewMapControlOptions.setOpened(val > 0);
                    mapOpt.setOverviewMapControlOptions(overviewMapControlOptions);
                    optChanged = true;
                }
            }
        }

        if (uidl.hasAttribute(ATTR_PAN_CONTROL)) {
            int val = uidl.getIntAttribute(ATTR_PAN_CONTROL);
            if (val == -1) {
                if (mapOpt.getPanControl()) {
                    mapOpt.setPanControl(false);
                    optChanged = true;
                }
            } else if (val >= 0) {
                if (mapOpt.getPanControlOptions() == null || mapOpt.getPanControlOptions().getPosition().value() != val) {
                    mapOpt.setPanControl(true);
                    PanControlOptions panControlOptions = PanControlOptions.newInstance();
                    panControlOptions.setPosition(ControlPosition.fromValue(val));
                    mapOpt.setPanControlOptions(panControlOptions);
                    optChanged = true;
                    //log(1, "setPanControlOptions");
                }
            }
        }

        if (uidl.hasAttribute(ATTR_ROTATE_CONTROL)) {
            int val = uidl.getIntAttribute(ATTR_ROTATE_CONTROL);
            if (val == -1) {
                if (mapOpt.getRotateControl()) {
                    mapOpt.setRotateControl(false);
                    optChanged = true;
                }
            } else if (val >= 0) {
                if (mapOpt.getRotateControlOptions() == null || mapOpt.getRotateControlOptions().getPosition().value() != val) {
                    mapOpt.setRotateControl(true);
                    RotateControlOptions rotateControlOptions = RotateControlOptions.newInstance();
                    rotateControlOptions.setPosition(ControlPosition.fromValue(val));
                    mapOpt.setRotateControlOptions(rotateControlOptions);
                    optChanged = true;
                }
            }
        }

        if (uidl.hasAttribute(ATTR_SCALE_CONTROL)) {
            int val = uidl.getIntAttribute(ATTR_SCALE_CONTROL);
            if (val == -1) {
                if (mapOpt.getScaleControl()) {
                    mapOpt.setScaleControl(false);
                    optChanged = true;
                }
            } else if (val >= 0) {
                if (mapOpt.getScaleControlOptions() == null || mapOpt.getScaleControlOptions().getPosition().value() != val) {
                    mapOpt.setScaleControl(true);
                    ScaleControlOptions scaleControlOptions = ScaleControlOptions.newInstance();
                    scaleControlOptions.setPosition(ControlPosition.fromValue(val));
                    mapOpt.setScaleControlOptions(scaleControlOptions);
                    optChanged = true;
                }
            }
        }

        if (uidl.hasAttribute(ATTR_STREET_VIEW_CONTROL)) {
            int val = uidl.getIntAttribute(ATTR_STREET_VIEW_CONTROL);
            if (val == -1) {
                if (mapOpt.getStreetViewControl()) {
                    mapOpt.setStreetViewControl(false);
                    optChanged = true;
                }
            } else if (val >= 0) {
                if (mapOpt.getStreetViewControlOptions() == null || mapOpt.getStreetViewControlOptions().getPosition().value() != val) {
                    mapOpt.setStreetViewControl(true);
                    StreetViewControlOptions streetviewControlOptions = StreetViewControlOptions.newInstance();
                    streetviewControlOptions.setPosition(ControlPosition.fromValue(val));
                    mapOpt.setStreetViewControlOptions(streetviewControlOptions);
                    optChanged = true;
                }
            }
        }

        if (uidl.hasAttribute(ATTR_ZOOM_CONTROL)) {
            int val = uidl.getIntAttribute(ATTR_ZOOM_CONTROL) % 100;
            int style = (int) Math.floor(uidl.getIntAttribute(ATTR_ZOOM_CONTROL) / 100);
            if (val == -1) {
                if (mapOpt.getZoomControl()) {
                    mapOpt.setZoomControl(false);
                    optChanged = true;
                }
            } else if (val >= 0) {
                if (mapOpt.getZoomControlOptions() == null || mapOpt.getZoomControlOptions().getPosition().value() != val) {
                    mapOpt.setZoomControl(true);
                    ZoomControlOptions zoomControlOptions = ZoomControlOptions.newInstance();
                    zoomControlOptions.setPosition(ControlPosition.fromValue(val));
                    String styles[] = { "DEFAULT", "LARGE", "SMALL" };
                    zoomControlOptions.setStyle(ZoomControlStyle.fromValue(styles[style]));
                    mapOpt.setZoomControlOptions(zoomControlOptions);
                    optChanged = true;
                }
            }
        }

        if (optChanged) {
            map.setOptions(mapOpt);
        }

        if (uidl.hasVariable(VAR_CENTER)) {
            LatLng center = strToLatLng(uidl.getStringVariable(VAR_CENTER));
            if (!map.getCenter().equals(center)) {
                map.setCenter(center);
                mapOpt.setCenter(center);
            }
        }

        if (uidl.hasVariable(VAR_ZOOM)) {
            int zoom = uidl.getIntVariable(VAR_ZOOM);
            if (zoom != map.getZoom()) {
                map.setZoom(zoom);
                mapOpt.setZoom(zoom);
            }
        }

        if (uidl.hasVariable(VAR_BOUNDS_NE)) {
            LatLng sw = strToLatLng(uidl.getStringVariable(VAR_BOUNDS_SW));
            LatLng ne = strToLatLng(uidl.getStringVariable(VAR_BOUNDS_NE));
            LatLngBounds fitBounds = LatLngBounds.newInstance(sw, ne);
            map.fitBounds(fitBounds);
        }

        //        for (MapControl control : MapControl.values()) {
        //            if (uidl.hasAttribute(control.name())) {
        //                if (!controls.contains(control)) {
        //                    map.addControl(newControl(control));
        //                    controls.add(control);
        //                }
        //            } else if (controls.contains(control)) {
        //                map.removeControl(newControl(control));
        //                controls.add(control);
        //            }
        //        }

        if (uidl.hasAttribute(ATTR_MARKER_RESOURCE_FILE)) {
            String markerUrl = client.translateVaadinUri(uidl.getStringAttribute(ATTR_MARKER_RESOURCE_FILE));
            if (markerUrl != null) {
                Scheduler.get().scheduleDeferred(new MarkerRetrieveCommand(markerUrl));
            }
        }

        if (uidl.hasAttribute(ATTR_INFOWINDOW_MARKER)) {
            // When adding the markers we get the ID from JSONString.toString()
            // which includes quotation marks around the ID.
            String markerId = uidl.getStringAttribute(ATTR_INFOWINDOW_MARKER);

            Marker marker = knownMarkers.get(markerId);
            for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
                final UIDL u = (UIDL) it.next();
                if (!u.getTag().equals(TAG_TABS)) {
                    continue;
                }
                if (u.getChildCount() == 0) {
                    log(1, "No contents for info window");
                } else if (u.getChildCount() == 1) {
                    // Only one component in the info window -> no tabbing
                    UIDL paintableUIDL = u.getChildUIDL(0).getChildUIDL(0);
                    Paintable paintable = client.getPaintable(paintableUIDL);

                    InfoWindowOptions options = InfoWindowOptions.newInstance();
                    options.setContent((Widget) paintable);
                    options.setPosition(marker.getPosition());

                    InfoWindow iw = InfoWindow.newInstance(options);
                    iw.open(map, marker);

                    if (knownInfowindow.containsKey(markerId)) {
                        knownInfowindow.get(markerId).close();
                    }
                    knownInfowindow.remove(markerId);
                    knownInfowindow.put(markerId, iw);
                    // Update components in the info window after adding them to
                    // DOM so that size calculations can succeed
                    paintable.updateFromUIDL(paintableUIDL, client);
                } else {
                    int tabs = u.getChildCount();
                    // More than one component, show them in info window tabs
                    InfoWindow[] infoTabs = new InfoWindow[tabs];

                    Paintable[] paintables = new Paintable[tabs];
                    UIDL[] uidls = new UIDL[tabs];

                    int selectedId = 0;
                    for (int i = 0; i < u.getChildCount(); i++) {
                        UIDL childUIDL = u.getChildUIDL(i);
                        if (selectedId == 0 && childUIDL.getBooleanAttribute(ATTR_TAB_SELECTED)) {
                            selectedId = i;
                        }

                        // String label = childUIDL.getStringAttribute(ATTR_TAB_LABEL);

                        UIDL paintableUIDL = childUIDL.getChildUIDL(0);
                        Paintable paintable = client.getPaintable(paintableUIDL);

                        paintables[i] = paintable;
                        uidls[i] = paintableUIDL;

                        InfoWindowOptions options = InfoWindowOptions.newInstance();
                        options.setContent((Widget) paintable);
                        infoTabs[i] = InfoWindow.newInstance(options);

                        //infoTabs[i] = new InfoWindowContent.InfoWindowTab(label, (Widget) paintable);
                    }

                    infoTabs[selectedId].open(map, marker);

                    // TODO Check if works
                    knownInfowindow.put(markerId, infoTabs[selectedId]);

                    // Update paintables after adding them to DOM so that
                    // size calculations can succeed
                    for (int i = 0; i < paintables.length; i++) {
                        paintables[i].updateFromUIDL(uidls[i], client);
                    }
                }
            }
        }

        if (uidl.hasAttribute(ATTR_CLEAR_MAP_TYPES))
            map.getMapTypeRegistry().unbindAll();

        Map<Integer, UIDL> newPolys = new HashMap<Integer, UIDL>();

        // Process polygon/polyline overlays and map types
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL u = (UIDL) it.next();
            if (u.getTag().equals(TAG_OVERLAYS)) {

                long nodeStart = System.currentTimeMillis();

                for (final Iterator<Object> iter = u.getChildIterator(); iter.hasNext();) {
                    final UIDL polyUIDL = (UIDL) iter.next();
                    newPolys.put(polyUIDL.getIntAttribute(ATTR_OVERLAY_ID), polyUIDL);
                }

                log(1, "Polygon overlays readed in " + (System.currentTimeMillis() - nodeStart) + "ms - polys count: " + newPolys.size());
            } else if (u.getTag().equals(TAG_MAP_TYPES)) {
                long nodeStart = System.currentTimeMillis();

                for (final Iterator<Object> iter = u.getChildIterator(); iter.hasNext();) {
                    UIDL maptypeUIDL = (UIDL) iter.next();
                    map.getMapTypeRegistry().set(maptypeUIDL.getStringAttribute(ATTR_MAPTYPE_NAME), mapTypeFromUIDL(maptypeUIDL));
                }

                log(1, "Map types processed in " + (System.currentTimeMillis() - nodeStart) + "ms");
            }
        }

        long nodeStart = System.currentTimeMillis();

        // Remove deleted overlays from the map
        List<Integer> removedPolyIds = new ArrayList<Integer>();
        for (Entry<Integer, MVCObject<?>> entry : knownPolygons.entrySet()) {
            if (!newPolys.containsKey(entry.getKey())) {
                removeMVCObject(entry.getValue());
                removedPolyIds.add(entry.getKey());
            }
        }

        // ... and from the map. Can't remove them while iterating the collection
        for (Integer id : removedPolyIds)
            knownPolygons.remove(id);

        // Add new overlays
        for (Entry<Integer, UIDL> entry : newPolys.entrySet()) {
            if (!knownPolygons.containsKey(entry.getKey())) {
                MVCObject<?> poly = null;
                if (entry.getValue().hasAttribute(ATTR_OVERLAY_FILLCOLOR)) {
                    poly = polygonFromUIDL(entry.getValue());
                } else {
                    poly = polylineFromUIDL(entry.getValue());
                }

                if (poly != null) {
                    knownPolygons.put(entry.getKey(), poly);
                    //addMVCObject(entry.getValue());
                }
            }
        }

        log(1, "Polygon overlays processed in " + (System.currentTimeMillis() - nodeStart) + "ms");

        if (uidl.hasAttribute(ATTR_INFOWINDOW_CLOSE)) {
            if (uidl.getBooleanAttribute(ATTR_INFOWINDOW_CLOSE)) {
                for (InfoWindow iw : knownInfowindow.values())
                    iw.close();
                knownInfowindow.clear();
            }
        }

        ignoreVariableChanges = false;

        if (uidl.hasAttribute(ATTR_REPORT_BOUNDS) && uidl.getBooleanAttribute(ATTR_REPORT_BOUNDS) == true) {
            reportMapBounds();
        }

        log(1, "IGoogleMap.updateFromUIDL() took " + (System.currentTimeMillis() - start) + "ms");
    }

    private ImageMapType mapTypeFromUIDL(UIDL maptypeUIDL) {
        int minZoom = maptypeUIDL.getIntAttribute(ATTR_MAPTYPE_MIN_ZOOM);
        int maxZoom = maptypeUIDL.getIntAttribute(ATTR_MAPTYPE_MAX_ZOOM);
        // String copyright = maptypeUIDL.getStringAttribute(ATTR_MAPTYPE_COPYRIGHT);
        String name = maptypeUIDL.getStringAttribute(ATTR_MAPTYPE_NAME);
        final String tileUrl = maptypeUIDL.getStringAttribute(ATTR_MAPTYPE_TILE_URL);
        // boolean isPng = maptypeUIDL.getBooleanAttribute(ATTR_MAPTYPE_IS_PNG);
        double opacity = maptypeUIDL.getDoubleAttribute(ATTR_MAPTYPE_OPACITY);

        ImageMapTypeOptions opts = ImageMapTypeOptions.newInstance();
        opts.setMaxZoom(maxZoom);
        opts.setMinZoom(minZoom);
        opts.setName(name);
        opts.setOpacity(opacity);
        opts.setTileSize(Size.newInstance(256d, 256d));
        opts.setTileUrl(new TileUrlCallBack() {

            @Override
            public String getTileUrl(Point point, int zoomLevel) {
                return tileUrl;
            }
        });

        return ImageMapType.newInstance(opts);
    }

    private Polyline polylineFromUIDL(UIDL polyUIDL) {
        String[] encodedPoints = polyUIDL.getStringAttribute(ATTR_OVERLAY_POINTS).split(" ");
        LatLng[] points = new LatLng[encodedPoints.length];
        for (int i = 0; i < encodedPoints.length; i++) {
            String[] p = encodedPoints[i].split(",");
            double lat = Double.parseDouble(p[0]);
            double lng = Double.parseDouble(p[1]);
            points[i] = LatLng.newInstance(lat, lng);
        }

        String color = polyUIDL.getStringAttribute(ATTR_OVERLAY_COLOR);
        int weight = polyUIDL.getIntAttribute(ATTR_OVERLAY_WEIGHT);
        double opacity = polyUIDL.getDoubleAttribute(ATTR_OVERLAY_OPACITY);
        boolean clickable = polyUIDL.getBooleanAttribute(ATTR_OVERLAY_CLICKABLE);

        JsArray<LatLng> simpleLatLngArr = ArrayHelper.toJsArray(points);
        PolylineOptions opts = PolylineOptions.newInstance();
        opts.setMap(map);
        opts.setPath(simpleLatLngArr);
        opts.setStrokeColor(color);
        opts.setStrokeOpacity(opacity);
        opts.setStrokeWeight(weight);
        opts.setClickable(clickable);

        return Polyline.newInstance(opts);
    }

    private Polygon polygonFromUIDL(UIDL polyUIDL) {
        String[] encodedPoints = polyUIDL.getStringAttribute(ATTR_OVERLAY_POINTS).split(" ");
        LatLng[] points = new LatLng[encodedPoints.length];
        for (int i = 0; i < encodedPoints.length; i++) {
            String[] p = encodedPoints[i].split(",");
            double lat = Double.parseDouble(p[0]);
            double lng = Double.parseDouble(p[1]);
            points[i] = LatLng.newInstance(lat, lng);
        }

        String color = polyUIDL.getStringAttribute(ATTR_OVERLAY_COLOR);
        int weight = polyUIDL.getIntAttribute(ATTR_OVERLAY_WEIGHT);
        double opacity = polyUIDL.getDoubleAttribute(ATTR_OVERLAY_OPACITY);
        String fillColor = polyUIDL.getStringAttribute(ATTR_OVERLAY_FILLCOLOR);
        double fillOpacity = polyUIDL.getDoubleAttribute(ATTR_OVERLAY_FILLOPACITY);
        boolean clickable = polyUIDL.getBooleanAttribute(ATTR_OVERLAY_CLICKABLE);

        JsArray<LatLng> simpleLatLngArr = ArrayHelper.toJsArray(points);
        PolygonOptions opts = PolygonOptions.newInstance();
        opts.setMap(map);
        opts.setFillColor(fillColor);
        opts.setFillOpacity(fillOpacity);
        opts.setPaths(simpleLatLngArr);
        opts.setStrokeColor(color);
        opts.setStrokeOpacity(opacity);
        opts.setStrokeWeight(weight);
        opts.setClickable(clickable);

        return Polygon.newInstance(opts);
    }

    private String getMarkerIconURL(Marker marker) {
        if (marker.getIcon_MarkerImage() == null)
            return null;

        return marker.getIcon_MarkerImage().getUrl();
    }

    public void onClick(ClickMapEvent event) {
        if (ignoreVariableChanges) {
            return;
        }

        // if (event.getOverlay() != null) {
        // return;
        // }

        client.updateVariable(paintableId, VAR_CLICK_POSITION, latLngToStr(event.getMouseEvent().getLatLng()), true);
    }

    public void onMoveEnd(DragEndMapEvent event) {
        if (ignoreVariableChanges) {
            return;
        }

        reportMapBounds();
    }

    private void reportMapBounds() {
        client.updateVariable(paintableId, VAR_ZOOM, map.getZoom(), false);
        client.updateVariable(paintableId, VAR_BOUNDS_NE, latLngToStr(map.getBounds().getNorthEast()), false);
        client.updateVariable(paintableId, VAR_BOUNDS_SW, latLngToStr(map.getBounds().getSouthWest()), false);
        client.updateVariable(paintableId, VAR_CENTER, latLngToStr(map.getCenter()), true);
    }

    public void onDragEnd(DragEndMapEvent event) {
        Marker marker = (Marker) event.getSource();
        try {
            marker = event.getProperties().getObject(VAR_MARKER_CLICKED).cast();
        } catch (TypeException e) {
        }
        if (marker == null)
            return;

        Set<String> keys = knownMarkers.keySet();
        for (String key : keys) {
            // Find the key for the moved marker
            if (knownMarkers.get(key).equals(marker)) {
                client.updateVariable(paintableId, VAR_MARKER_MOVED_ID, key, false);
                client.updateVariable(paintableId, VAR_MARKER_MOVED_LAT, marker.getPosition().getLatitude(), false);
                client.updateVariable(paintableId, VAR_MARKER_MOVED_LNG, marker.getPosition().getLongitude(), true);
                log(1, "marker onDragEnd");
                break;
            }
        }
    }

    protected void markerClicked(String mId) {
        client.updateVariable(paintableId, VAR_MARKER_CLICKED, mId, true);
    }

    // working on direction
    @SuppressWarnings("unused")
    private void drawDirections(LatLng origin, LatLng destination, TravelMode travelMode, List<LatLng> wps) {
        DirectionsRequest request = DirectionsRequest.newInstance();
        request.setOrigin(origin);
        request.setDestination(destination);
        request.setTravelMode(travelMode);
        request.setOptimizeWaypoints(true);

        // Stop over
        if (wps != null && !wps.isEmpty()) {
            JsArray<DirectionsWaypoint> waypoints = JsArray.createArray().cast();

            for (LatLng wp : wps) {
                DirectionsWaypoint waypoint = DirectionsWaypoint.newInstance();
                waypoint.setStopOver(true);
                waypoint.setLocation(wp);
                waypoints.push(waypoint);
            }

            request.setWaypoints(waypoints);
        }

        DirectionsRendererOptions options = DirectionsRendererOptions.newInstance();
        final DirectionsRenderer directionsDisplay = DirectionsRenderer.newInstance(options);
        directionsDisplay.setMap(map);

        DirectionsService o = DirectionsService.newInstance();
        o.route(request, new DirectionsResultHandler() {
            public void onCallback(DirectionsResult result, DirectionsStatus status) {
                // TODO fire event
                if (status == DirectionsStatus.OK) {
                    directionsDisplay.setDirections(result);
                } else if (status == DirectionsStatus.INVALID_REQUEST) {

                } else if (status == DirectionsStatus.MAX_WAYPOINTS_EXCEEDED) {

                } else if (status == DirectionsStatus.NOT_FOUND) {

                } else if (status == DirectionsStatus.OVER_QUERY_LIMIT) {

                } else if (status == DirectionsStatus.REQUEST_DENIED) {

                } else if (status == DirectionsStatus.UNKNOWN_ERROR) {

                } else if (status == DirectionsStatus.ZERO_RESULTS) {

                }
            }
        });
    }

    @SuppressWarnings("unused")
    private void getDistance(LatLng origin, LatLng destination, TravelMode travelMode) {
        DistanceMatrixRequest request = DistanceMatrixRequest.newInstance();
        request.setOrigins(ArrayHelper.toJsArray(origin));
        request.setDestinations(ArrayHelper.toJsArray(destination));
        request.setTravelMode(travelMode);

        DistanceMatrixService o = DistanceMatrixService.newInstance();
        o.getDistanceMatrix(request, new DistanceMatrixRequestHandler() {
            public void onCallback(DistanceMatrixResponse response, DistanceMatrixStatus status) {
                // TODO fire event
                if (status == DistanceMatrixStatus.INVALID_REQUEST) {

                } else if (status == DistanceMatrixStatus.MAX_DIMENSIONS_EXCEEDED) {

                } else if (status == DistanceMatrixStatus.MAX_ELEMENTS_EXCEEDED) {

                } else if (status == DistanceMatrixStatus.OK) {

                    JsArray<DistanceMatrixResponseRow> rows = response.getRows();

                    DistanceMatrixResponseRow d = rows.get(0);
                    JsArray<DistanceMatrixResponseElement> elements = d.getElements();
                    for (int i = 0; i < elements.length(); i++) {
                        DistanceMatrixResponseElement e = elements.get(i);
                        Distance distance = e.getDistance();
                        Duration duration = e.getDuration();
                        String html = "&nbsp;&nbsp;Distance=" + distance.getText() + " Duration=" + duration.getText() + " ";
                    }

                } else if (status == DistanceMatrixStatus.OVER_QUERY_LIMIT) {

                } else if (status == DistanceMatrixStatus.REQUEST_DENIED) {

                } else if (status == DistanceMatrixStatus.UNKNOWN_ERROR) {

                }

            }
        });

    }

    private void log(int level, String message) {
        if (level <= logLevel) {
            // Show message in GWT console
            System.out.println(message);

            // And also in Vaadin debug window
            VConsole.log(message);
        }
    }

    class InfoWindowOpener implements ClickMapHandler {
        private final String markerId;

        InfoWindowOpener(String markerId) {
            super();
            this.markerId = markerId;
        }

        @Override
        public void onEvent(ClickMapEvent event) {
            markerClicked(markerId);
        }
    }

    class MarkerRetrieveCommand implements Command {
        private final String markerUrl;

        MarkerRetrieveCommand(String markerUrl) {
            super();
            this.markerUrl = markerUrl;
        }

        @Override
        public void execute() {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, markerUrl);

            try {
                builder.setTimeoutMillis(2000);

                markerRequestSentAt = System.currentTimeMillis();

                builder.sendRequest(null, new RequestCallback() {
                    @Override
                    public void onError(Request request, Throwable e) {
                        if (e instanceof RequestTimeoutException) {
                            log(1, "Timeout fetching marker data: " + e.getMessage());
                        } else {
                            log(1, "Error fetching marker data: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        String markerJSON = response.getText();

                        log(1, "" + markerJSON.length() + " bytes of marker response got in " + (System.currentTimeMillis() - markerRequestSentAt) + "ms");

                        JSONArray array = null;
                        try {
                            long start = System.currentTimeMillis();
                            JSONValue json = JSONParser.parseLenient(markerJSON);
                            array = json.isArray();
                            log(1, "JSON parsed in " + (System.currentTimeMillis() - start) + "ms");
                            if (array == null) {
                                System.out.println("Marker JSON was not an array.");
                                return;
                            }

                            handleMarkerJSON(array);
                        } catch (Exception e) {
                            log(1, "Error parsing json: " + e.getMessage());
                        }
                    }
                });
            } catch (RequestException e) {
                log(1, "Failed to send the request: " + e.getMessage());
            }
        }

        private void handleMarkerJSON(JSONArray array) {
            synchronized (knownMarkers) {

                long startTime = System.currentTimeMillis();
                int initSize = knownMarkers.size();
                List<String> markersFromThisUpdate = new ArrayList<String>();
                for (int i = 0; i < array.size(); i++) {

                    JSONMarker jsMarker;
                    Marker marker = null;
                    boolean replaceMarker = false;

                    jsMarker = new JSONMarker(array.get(i).isObject());

                    if (jsMarker.isNull()) {
                        continue;
                    }

                    // Read marker id
                    if (jsMarker.getId() == null) {
                        continue;
                    }

                    // Add maker to list of markers in this update
                    markersFromThisUpdate.add(jsMarker.getId());

                    if (knownMarkers.containsKey(jsMarker.getId())) {
                        marker = knownMarkers.get(jsMarker.getId());

                        if (marker == null)
                            continue;

                        // all set methods are useless because the marker is replaced with new one
                        // TODO find a way to fix setting methods

                        // if title is changed
                        if (jsMarker.getTitle() != null && !jsMarker.getTitle().equals(marker.getTitle())) {
                            replaceMarker = true;
                            log(1, "Title changed: " + marker.getTitle());
                        }

                        if (jsMarker.getLat() != null && jsMarker.getLng() != null) {
                            LatLng newPos = LatLng.newInstance(jsMarker.getLat(), jsMarker.getLng());
                            if (newPos.equals(marker.getPosition())) {
                                marker.setPosition(newPos);
                                replaceMarker = true;
                            }
                        }

                        if (jsMarker.isDraggable() != marker.getDraggable()) {
                            marker.setDraggable(jsMarker.isDraggable());
                            replaceMarker = true;
                        }

                        if (jsMarker.isVisible() != marker.getVisible()) {
                            marker.setVisible(jsMarker.isVisible());
                            replaceMarker = true;
                        }

                        if (jsMarker.getIcon() != null) {
                            if (!jsMarker.getIcon().equals(getMarkerIconURL(marker))) {
                                MarkerImage icon = MarkerImage.newInstance(jsMarker.getIcon());
                                if (jsMarker.hasOriginPoint())
                                    icon.setOrigin(Point.newInstance(jsMarker.getIconAnchorX(), jsMarker.getIconAnchorY()));
                                if (jsMarker.hasAnchorPoint())
                                    icon.setAnchor(Point.newInstance(jsMarker.getIconAnchorX(), jsMarker.getIconAnchorY()));
                                marker.setIcon(icon);
                                replaceMarker = true;
                            } else {
                                if (jsMarker.hasOriginPoint()) {
                                    Point newOrigin = Point.newInstance(jsMarker.getIconOriginX(), jsMarker.getIconOriginY());
                                    if (marker.getIcon_MarkerImage() != null && !newOrigin.equals(marker.getIcon_MarkerImage().getOrigin())) {
                                        marker.getIcon_MarkerImage().setOrigin(newOrigin);
                                        replaceMarker = true;
                                    }
                                }
                                if (jsMarker.hasAnchorPoint()) {
                                    Point newAnchor = Point.newInstance(jsMarker.getIconAnchorX(), jsMarker.getIconAnchorY());
                                    if (marker.getIcon_MarkerImage() != null && !newAnchor.equals(marker.getIcon_MarkerImage().getAnchor())) {
                                        marker.getIcon_MarkerImage().setAnchor(newAnchor);
                                        replaceMarker = true;
                                    }
                                }
                            }
                        }

                        if (jsMarker.getAnimation() != null) {
                            Animation newAnimation = Animation.fromValue(jsMarker.getAnimation());
                            if (!newAnimation.equals(marker.getAnimation())) {
                                marker.setAnimation(newAnimation);
                                replaceMarker = true;
                            }
                        }

                    }

                    if (replaceMarker) {
                        log(1, "Replacing marker " + marker.getTitle());
                        markersFromThisUpdate.remove(marker);
                        marker.clear();
                    }

                    marker = jsMarker.createMarker();

                    if (marker != null) {
                        marker.setMap(map);

                        // Add dragEnd handlers to marker
                        marker.addDragEndHandler(new MarkerDragEndHandlers(marker));
                        marker.addClickHandler(new InfoWindowOpener(jsMarker.getId()));

                        knownMarkers.put(jsMarker.getId(), marker);
                    }

                }

                int newMarkers = knownMarkers.size() - initSize;

                long dur = System.currentTimeMillis() - startTime;

                if (newMarkers == 0) {
                    log(1, "No new markers added in " + dur + "ms.");
                } else {
                    log(1, "" + newMarkers + " markers added in " + dur + "ms: " + dur / newMarkers + "ms per marker");
                }

                // Remove markers that wasn't in the update (i.e. removed on server side)
                List<String> removedMarkers = new ArrayList<String>();
                for (String mID : knownMarkers.keySet()) {
                    if (!markersFromThisUpdate.contains(mID)) {
                        knownMarkers.get(mID).setMap((MapWidget) null);
                        removedMarkers.add(mID);
                    }
                }

                for (String mID : removedMarkers) {
                    knownMarkers.remove(mID);
                }
            }
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        wrapperPanel.setHeight(height);
        if (map != null) {
            map.setHeight(height);
            MapHandlerRegistration.trigger(map, MapEventType.RESIZE);
        } else {
        //    VConsole.error("Set height attempted before map initialized");
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        wrapperPanel.setWidth(width);
        if (map != null) {
            map.setWidth(width);
            MapHandlerRegistration.trigger(map, MapEventType.RESIZE);
        } else {
        //    VConsole.error("Set width attempted before map initialized");
        }
    }

    protected void removeMVCObject(MVCObject<?> obj) {
        if (obj instanceof Polygon)
            ((Polygon) obj).setMap(null);
        else if (obj instanceof Polyline)
            ((Polyline) obj).setMap(null);
    }

    protected void addMVCObject(MVCObject<?> obj) {
        if (obj instanceof Polygon)
            ((Polygon) obj).setMap(map);
        else if (obj instanceof Polyline)
            ((Polyline) obj).setMap(map);
    }

    public static LatLng strToLatLng(String latLngStr) {

        if (latLngStr == null) {
            return null;
        }

        String nums[] = latLngStr.split(", ");
        if (nums.length != 2) {
            return null;
        }

        double lat = Double.parseDouble(nums[0]);

        double lng = Double.parseDouble(nums[1]);

        return LatLng.newInstance(lat, lng);
    }

    public static String latLngToStr(LatLng latLng) {
        return latLng.getLatitude() + ", " + latLng.getLongitude();
    }

    class JSONMarker {

        private JSONObject obj;

        public JSONMarker(JSONObject obj) {
            super();
            this.obj = obj;
        }

        public boolean isNull() {
            return obj == null;
        }

        public JSONValue get(String key) {
            return obj.get(key);
        }

        public String getId() {
            return getString("mid");
        }

        public String getTitle() {
            return getString("title");
        }

        public Double getLat() {
            return getNumber("lat");
        }

        public Double getLng() {
            return getNumber("lng");
        }

        public Integer getAnimation() {
            return getNumber("animation").intValue();
        }

        public boolean isDraggable() {
            Boolean value = getBoolean("draggable");
            return value == null ? false : value;
        }

        public boolean isVisible() {
            Boolean value = getBoolean("visible");
            return value == null ? true : value;
        }

        public String getIcon() {
            return getString("icon");
        }

        public boolean hasAnchorPoint() {
            return getIconAnchorX() != null && getIconAnchorY() != null;
        }

        public Double getIconAnchorX() {
            return getNumber("iconAnchorX");
        }

        public Double getIconAnchorY() {
            return getNumber("iconAnchorY");
        }

        public boolean hasOriginPoint() {
            return getIconOriginX() != null && getIconOriginY() != null;
        }

        public Double getIconOriginX() {
            return getNumber("iconOriginX");
        }

        public Double getIconOriginY() {
            return getNumber("iconOriginY");
        }

        public String getInfoContent() {
            return getString("info");
        }

        private String getString(String key) {
            JSONValue value = get(key);
            if (value != null && value.isString() != null)
                return value.isString().stringValue();
            return null;
        }

        private Boolean getBoolean(String key) {
            JSONValue value = get(key);
            if (value != null && value.isBoolean() != null)
                return value.isBoolean().booleanValue();
            return null;
        }

        private Double getNumber(String key) {
            JSONValue value = get(key);
            if (value != null && value.isNumber() != null)
                return value.isNumber().doubleValue();
            return null;
        }

        public Marker createMarker() {
            return createMarker(this);
        }

        private Marker createMarker(JSONMarker jsMarker) {

            MarkerImage icon = null;
            if (jsMarker.getIcon() != null) {
                icon = MarkerImage.newInstance(jsMarker.getIcon());
                if (jsMarker.hasOriginPoint())
                    icon.setOrigin(Point.newInstance(jsMarker.getIconOriginX(), jsMarker.getIconOriginY()));
                if (jsMarker.hasAnchorPoint())
                    icon.setAnchor(Point.newInstance(jsMarker.getIconAnchorX(), jsMarker.getIconAnchorY()));

                log(1, "Icon URL '" + jsMarker.getIcon() + "' at anchor point (" + jsMarker.getIconAnchorX() + "," + jsMarker.getIconAnchorY() + ")");
            }

            log(1, "Title '" + jsMarker.getTitle() + "' position (" + jsMarker.getLat() + "," + jsMarker.getLng() + ") visible " + jsMarker.isVisible());

            MarkerOptions mopts = MarkerOptions.newInstance();
            mopts.setIcon(icon);
            mopts.setTitle(jsMarker.getTitle());
            mopts.setDraggable(jsMarker.isDraggable());
            mopts.setVisible(jsMarker.isVisible());
            if (jsMarker.getAnimation() == null || jsMarker.getAnimation().equals(""))
                mopts.setAnimation(Animation.STOPANIMATION);
            else
                mopts.setAnimation(Animation.fromValue(jsMarker.getAnimation()));

            final double lat = jsMarker.getLat();
            final double lng = jsMarker.getLng();

            if (lat < -90 || lat > 90) {
                log(1, "Invalid latitude for marker: " + lat);
                return null;
            }

            if (lng < -180 || lng > 180) {
                log(1, "Invalid latitude for marker: " + lat);
                return null;
            }
            mopts.setPosition(LatLng.newInstance(lat, lng));

            return Marker.newInstance(mopts);
        }
    }
}