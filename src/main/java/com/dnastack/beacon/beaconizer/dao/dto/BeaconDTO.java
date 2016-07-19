package com.dnastack.beacon.beaconizer.dao.dto;

/**
 * Created by patrickmagee on 2016-07-19.
 */
public class BeaconDTO {

    private String name;

    private String url;

    private String description;

    private String key;

    public BeaconDTO() {

    }

    public BeaconDTO(String name, String url, String description, String key) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



}
