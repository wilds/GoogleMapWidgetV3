package com.wildboar.vaadin.addon.googlemap.overlay;

import java.awt.geom.Point2D;

import com.vaadin.ui.Component;

public class BasicMarker implements Marker {

    private Long id;

    private boolean visible = true;

    private Point2D.Double latLng;

    private String iconUrl = null;

    private Point2D.Double iconAnchor;

    private Point2D.Double iconOrigin;

    private String title = null;

    private InfoWindowTab[] infoWindowContent = null;

    private boolean draggable = true;

    private int animation;

    public BasicMarker(Long id, Point2D.Double latLng, String title) {
        this.id = id;
        this.latLng = latLng;
        this.title = title;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Point2D.Double getLatLng() {
        return latLng;
    }

    public void setLatLng(Point2D.Double latLng) {
        this.latLng = latLng;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String imageUrl) {
        iconUrl = imageUrl;
    }

    @Override
    public Point2D.Double getIconAnchor() {
        return iconAnchor;
    }

    public void setIconAnchor(Point2D.Double iconAnchor) {
        this.iconAnchor = iconAnchor;
    }

    @Override
    public Point2D.Double getIconOrigin() {
        return iconOrigin;
    }

    public void setIconOrigin(Point2D.Double iconOrigin) {
        this.iconOrigin = iconOrigin;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public InfoWindowTab[] getInfoWindowContent() {
        return infoWindowContent;
    }

    public void setInfoWindowContent(InfoWindowTab[] tabs) {
        infoWindowContent = tabs;
    }

    public void setInfoWindowContent(Component parent, Component component) {
        infoWindowContent = new InfoWindowTab[] { new InfoWindowTab(parent, component) };
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Marker))
            return false;
        Marker m = (Marker) obj;
        return getId().equals(m.getId());
    }

    @Override
    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

}