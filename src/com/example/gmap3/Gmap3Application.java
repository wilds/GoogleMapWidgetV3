package com.example.gmap3;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.wildboar.vaadin.addon.googlemap.overlay.BasicMarker;
import com.wildboar.vaadin.addon.googlemap.overlay.Marker;
import com.wildboar.vaadin.addon.googlemap.overlay.Polygon;
import com.wildboar.vaadin.addon.googlemap.server.Constant;
import com.wildboar.vaadin.addon.googlemap.server.GoogleMap;
import com.wildboar.vaadin.addon.googlemap.server.GoogleMap.MapClickListener;
import com.wildboar.vaadin.addon.googlemap.server.GoogleMap.MapMoveListener;
import com.wildboar.vaadin.addon.googlemap.server.GoogleMap.MarkerClickListener;
import com.wildboar.vaadin.addon.googlemap.server.GoogleMap.MarkerMovedListener;

@SuppressWarnings("serial")
public class Gmap3Application extends Application {

    private boolean toggle = true;
    private long id = 0;

    @Override
    public void init() {
        Window mainWindow = new Window("Gmap3 Application");
        final GoogleMap googleMap = new GoogleMap(this, new Point2D.Double(16.247449, 39.306592), 14);
        googleMap.setWidth("1000px");
        googleMap.setHeight("400px");
        googleMap.setControls(Constant.CONTROL_INVISIBLE, Constant.CONTROL_POSITION_TOP_RIGHT, Constant.CONTROL_INVISIBLE, Constant.CONTROL_INVISIBLE, Constant.CONTROL_POSITION_TOP_RIGHT, Constant.CONTROL_POSITION_TOP_RIGHT + Constant.ZOOM_CONTROL_STYLE_LARGE);
        googleMap.setClientLogLevel(100);
        mainWindow.addComponent(googleMap);
        setMainWindow(mainWindow);

        googleMap.addListener(new MapClickListener() {

            @Override
            public void mapClicked(Double clickPos) {
                System.out.println("map clicked");
                long cid = ++id;
                Marker m = new BasicMarker(cid, clickPos, "Marker " + cid);
                googleMap.addMarker(m);
            }
        });

        googleMap.addListener(new MapMoveListener() {

            @Override
            public void mapMoved(int newZoomLevel, Double newCenter, Double boundsNE, Double boundsSW) {
                System.out.println("map moved " + newZoomLevel + "    " + newCenter);
            }
        });

        googleMap.addListener(new MarkerClickListener() {

            @Override
            public void markerClicked(Marker clickedMarker) {
                System.out.println("marker clicked " + clickedMarker.getTitle());
            }
        });

        googleMap.addListener(new MarkerMovedListener() {

            @Override
            public void markerMoved(Marker movedMarker) {
                System.out.println("marker clicked " + movedMarker.getTitle() + " " + movedMarker.getLatLng().getX() + " " + movedMarker.getLatLng().getY());
            }
        });

        HorizontalLayout menu = new HorizontalLayout();

        Button buttonAddMarker = new Button("Add marker (custom icon)");
        buttonAddMarker.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                long cid = ++id;
                BasicMarker m = new BasicMarker(cid, googleMap.getCenter(), "Marker " + cid);
                m.setIconUrl("http://cinnamonthoughts.org/wp-content/uploads/2010/01/Custom-Marker-Avatar.png");
                m.setAnimation(Constant.ANIMATION_BOUNCE);
                googleMap.addMarker(m);

                m.setInfoWindowContent(googleMap, new Label("test"));
            }
        });
        menu.addComponent(buttonAddMarker);

        Button buttonAddPolyline = new Button("Add polyline");
        buttonAddPolyline.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Double[] points = new Double[2];
                points[0] = new Point2D.Double(16.247449 + (Math.random() - 0.5) / 10, 39.306592 + Math.random() / 10);
                points[1] = new Point2D.Double(16.347449 + (Math.random() - 0.5) / 10, 39.306592 + Math.random() / 10);
                Polygon p = new Polygon(++id, points);
                p.setColor("#0000FF");
                googleMap.addPolyOverlay(p);
            }
        });
        menu.addComponent(buttonAddPolyline);

        Button buttonRemovePolyline = new Button("Remove polylines");
        buttonRemovePolyline.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                googleMap.removeAllOverlays();
            }
        });
        menu.addComponent(buttonRemovePolyline);

        Button buttonAddPolygon = new Button("Add polygon");
        buttonAddPolygon.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Double[] points = new Double[3];
                points[0] = new Point2D.Double(16.257449, 39.316592);
                points[1] = new Point2D.Double(16.267449, 39.306592);
                points[2] = new Point2D.Double(16.277449, 39.306592);
                Polygon p = new Polygon(1003l, points);
                p.setFillColor("#FF00FF");
                googleMap.addPolyOverlay(p);
            }
        });
        menu.addComponent(buttonAddPolygon);

        Button buttonClearAll = new Button("Clear all");
        buttonClearAll.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                googleMap.removeAllMarkers();
                googleMap.removeAllOverlays();
            }
        });
        menu.addComponent(buttonClearAll);

        Button buttonToggleMapSize = new Button("Toggle map size");
        buttonToggleMapSize.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (!toggle)
                    googleMap.setWidth("1000px");
                else
                    googleMap.setWidth("600px");
                toggle = !toggle;
            }
        });
        menu.addComponent(buttonToggleMapSize);

        Button buttonCloseInfoWindow = new Button("Close Infowindow");
        buttonCloseInfoWindow.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                googleMap.closeInfoWindow();
            }
        });
        menu.addComponent(buttonCloseInfoWindow);

        Button buttonShowAllMarker = new Button("Pan to markers");
        buttonShowAllMarker.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                googleMap.showAllMarker();
            }
        });
        menu.addComponent(buttonShowAllMarker);

        mainWindow.addComponent(menu);
    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        super.terminalError(event);
        event.getThrowable().printStackTrace();
    }

}
