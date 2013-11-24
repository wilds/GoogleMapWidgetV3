package com.wildboar.vaadin.addon.googlemap.overlay;

import java.awt.geom.Point2D;

/**
 * @author Henri Muurimaa
 */
public interface Marker {
    public Long getId();

    public boolean isVisible();

    public Point2D.Double getLatLng();

    public String getIconUrl();

    public Point2D.Double getIconAnchor();

    public String getTitle();

    public InfoWindowTab[] getInfoWindowContent();

    public boolean isDraggable();

    public int getAnimation();
}