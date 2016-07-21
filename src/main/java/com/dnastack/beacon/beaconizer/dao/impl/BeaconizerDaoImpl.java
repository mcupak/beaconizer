/*
 * The MIT License
 *
 * Copyright 2014 Patrick Magee (patrickmageee@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dnastack.beacon.beaconizer.dao.impl;

import com.dnastack.beacon.beaconizer.dao.api.BeaconizerDao;
import com.dnastack.beacon.beaconizer.exceptions.BeaconNotFoundException;
import com.dnastack.beacon.exceptions.BeaconException;
import com.dnastack.beacon.utils.AdapterConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
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
 * BeaconizerDao implementation. Default implementation which loads into memory a list of beacons defined
 * in a json configuration file.
 *
 * @author patmagee
 */
@Singleton
public class BeaconizerDaoImpl implements BeaconizerDao {

    private static final String CONFIG_FILE = "beacons.json";
    Map<String, AdapterConfig> beacons;

    /**
     * Initialize the Beacons and load them into memopry from file
     */
    @PostConstruct
    public void init() {

        try {
            String jsonString = resourceParser();
            List<AdapterConfig> adapters = parseBeacons(jsonString);

            if (adapters.size() < 1) {
                throw new BeaconException("No beacons defined in beacon.json");
            }

            beacons = new HashMap<>();

            for (AdapterConfig adapter : adapters) {
                beacons.put(adapter.getName(), adapter);
            }
        } catch (Exception e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdapterConfig find(String name) throws BeaconNotFoundException {
        AdapterConfig beacon = beacons.get(name);
        if (beacon == null) {
            throw new BeaconNotFoundException("Could not find beacon with name: " + name);
        }
        return beacon;
    }

    public List<String> listRegisteredBeacons() {
        Set<String> keys = beacons.keySet();
        return new ArrayList<>(keys);
    }

    /**
     * Parse the resource file containing the beacon definition and convert it to a json string
     *
     * @return JSON string
     * @throws URISyntaxException
     * @throws IOException
     */
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

    /**
     * Given a json string, parse the input and convert it to BeaconDTO objects
     *
     * @param json json string from resourceParser
     * @return List of BeaconDTO's
     */
    private List<AdapterConfig> parseBeacons(String json) {
        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<AdapterConfig>>() {
        }.getType();

        List<AdapterConfig> adapters = gson.fromJson(json, type);

        return adapters;
    }
}
