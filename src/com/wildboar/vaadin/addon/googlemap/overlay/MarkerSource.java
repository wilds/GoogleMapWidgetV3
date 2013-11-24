package com.wildboar.vaadin.addon.googlemap.overlay;

import java.util.List;

import com.wildboar.vaadin.addon.googlemap.server.GoogleMap;
import com.wildboar.vaadin.addon.googlemap.server.Pair;


public interface MarkerSource {
    public List<Marker> getMarkers();

    public boolean addMarker(Marker newMarker);

    public void registerEvents(GoogleMap map);

    public byte[] getMarkerJSON();

    public Marker getMarker(String markerId);

    public Pair<java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double> getBoundingBox();
}