package com.dnastack.beacon.beaconizer.dao.impl;

import com.dnastack.beacon.beaconizer.dao.api.BeaconizerDao;
import com.dnastack.beacon.beaconizer.dao.dto.BeaconDTO;
import com.dnastack.beacon.beaconizer.exceptions.BeaconException;
import com.dnastack.beacon.beaconizer.exceptions.BeaconNotFoundException;
import com.dnastack.beacon.beaconizer.util.BeaconRequester;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by patrickmagee on 2016-07-20.
 */
@Singleton
public class BeaconizerDaoImpl implements BeaconizerDao {

    private static final String CONFIG_FILE = "beacons.json";
    Map<String, BeaconRequester> beacons;

    @PostConstruct
    public void init() {

        try {
            String jsonString = resourceParser();
            java.util.List<BeaconDTO> listBeaconDTOs = parseBeacons(jsonString);

            if (listBeaconDTOs.size() < 1) {
                throw new BeaconException("No beacons defined in beacon.json");
            }

            beacons = new HashMap<>();

            for (BeaconDTO beaconDTO : listBeaconDTOs) {
                validateBeacon(beaconDTO);

                beacons.put(beaconDTO.getName(), new BeaconRequester(beaconDTO));
            }
        } catch (Exception e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    @Override
    public BeaconRequester find(String name) throws BeaconNotFoundException {
        BeaconRequester beacon = beacons.get(name);
        if (beacon == null) {
            throw new BeaconNotFoundException("Could not find beacon with name: " + name);
        }
        return beacon;
    }

    @Override
    public List<BeaconRequester> list() {
        List<BeaconRequester> beaconList = new ArrayList<>();
        Set<String> keys = beacons.keySet();
        for (String key : keys) {
            beaconList.add(beacons.get(key));
        }

        return beaconList;
    }


    private String resourceParser() throws URISyntaxException, IOException {
        ClassLoader cl = getClass().getClassLoader();

        InputStream stream = cl.getResource(CONFIG_FILE).openStream();
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);

        String line;
        String jsonString = "";
        while ((line = br.readLine()) != null) {
            jsonString += line;
        }

        return jsonString;
    }


    private java.util.List<BeaconDTO> parseBeacons(String json) {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<java.util.List<BeaconDTO>>() {
        }.getType();

        java.util.List<BeaconDTO> beaconDTOs = gson.fromJson(json, type);

        return beaconDTOs;
    }

    private void validateBeacon(BeaconDTO beaconDTO) throws BeaconException {
        if (beaconDTO.getName() == null) {
            throw new BeaconException("BeaconDTO name is missing");
        }
        if (beaconDTO.getUrl() == null) {
            throw new BeaconException("BeaconDTO Url is missing");
        }
    }
}
