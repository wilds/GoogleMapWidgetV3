package com.wildboar.vaadin.addon.googlemap.overlay;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wildboar.vaadin.addon.googlemap.server.BoundingBox;
import com.wildboar.vaadin.addon.googlemap.server.GoogleMap;
import com.wildboar.vaadin.addon.googlemap.server.Pair;


public class BasicMarkerSource implements MarkerSource, Serializable {
    private static final long serialVersionUID = -803448463650898130L;

    private final List<Marker> markers = new ArrayList<Marker>();

    @Override
    public List<Marker> getMarkers() {
        return markers;
    }

    @Override
    public Pair<Point2D.Double, Point2D.Double> getBoundingBox() {
        BoundingBox bb = null;
        for (Marker m : markers) {
            if (bb == null)
                bb = new BoundingBox(m.getLatLng());
            else
                bb.addPoint(m.getLatLng());
        }
        if (bb == null)
            return null;
        return bb.getBounds();
    }

    @Override
    public boolean addMarker(Marker newMarker) {
        if (markers.contains(newMarker)) {
            return false;
        }

        markers.add(newMarker);

        return true;
    }

    @Override
    public byte[] getMarkerJSON() {
        // 1000 markers, using String concatenation: 8100ms
        // using StringBuilder: 17ms :)
        StringBuilder markerJSON = new StringBuilder();

        for (int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);

            markerJSON.append("{\"mid\":\"").append(marker.getId());
            markerJSON.append("\",\"lat\":").append(marker.getLatLng().y);
            markerJSON.append(",\"lng\":").append(marker.getLatLng().x);

            // Escape single and double quotes
            markerJSON.append(",\"title\":\"").append(marker.getTitle().replaceAll("'", "\'").replaceAll("\"", "\\\\\""));

            markerJSON.append("\",\"visible\":").append(marker.isVisible());
            markerJSON.append(",\"info\":").append(marker.getInfoWindowContent() != null);
            markerJSON.append(",\"draggable\":").append(marker.isDraggable());
            markerJSON.append(",\"animation\":").append(marker.getAnimation());

            if (marker.getIconUrl() != null) {
                markerJSON.append(",\"icon\":\"").append(marker.getIconUrl() + "\"");
                if (marker.getIconAnchor() != null) {
                    markerJSON.append(",\"iconAnchorX\":").append(marker.getIconAnchor().x);
                    markerJSON.append(",\"iconAnchorY\":").append(marker.getIconAnchor().y);
                }
                if (marker.getIconOrigin() != null) {
                    markerJSON.append(",\"iconOriginX\":").append(marker.getIconAnchor().x);
                    markerJSON.append(",\"iconOriginY\":").append(marker.getIconAnchor().y);
                }
            }

            markerJSON.append("}");
            if (i != markers.size() - 1) {
                markerJSON.append(",");
            }
        }

        try {
            return ("[" + markerJSON + "]").getBytes("UTF-8");
        } catch (Exception e) {
            return ("[" + markerJSON + "]").getBytes();
        }
    }

    @Override
    public void registerEvents(GoogleMap map) {
        // This marker source implementation is not interested in map events
    }

    @Override
    public Marker getMarker(String markerId) {
        // TODO The marker collection should be a map...
        for (Marker marker : markers) {
            if (marker.getId().toString().equals(markerId)) {
                return marker;
            }
        }

        return null;
    }
}