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
package com.dnastack.beacon.beaconizer.util;

import com.dnastack.beacon.adapter.api.BeaconAdapter;
import com.dnastack.beacon.beaconizer.dao.api.BeaconizerDao;
import com.dnastack.beacon.exceptions.BeaconException;
import com.dnastack.beacon.utils.AdapterConfig;
import lombok.NonNull;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeaconAdapterFactory for creating a new BeaconAdapter instance for a specific beacon. The adapter relies
 * on an AdapterConfig object to describe how an adapter should be created. It then creates and stores a single
 * instance of each adapter based on the name value passed in the AdapterConfig object
 *
 * @author patmagee
 */
@Singleton
public class BeaconAdapterFactory {

    @Inject
    BeaconizerDao dao;

    Map<String, BeaconAdapter> adapters = new ConcurrentHashMap<>();

    public List<String> listRegisteredBeacons(){
        return dao.listRegisteredBeacons();
    }

    /**
     * Get the adapter for registered to the passed name. If the adapter does not exist, attempt to create it
     *
     * @param name name of adapter instance
     * @return BeaconAdapter instance
     * @throws BeaconException
     */
    public BeaconAdapter getAdapter(@NonNull String name) throws BeaconException {
        BeaconAdapter adapter = adapters.get(name);
        if (adapter != null) {
            return adapter;
        }
        return newAdapter(name);
    }

    /**
     * Create a new BeaconAdapter and register it under the passed name
     *
     * @param name name of adapter instance
     * @return new BeaconAdapter
     * @throws BeaconException
     */
    private BeaconAdapter newAdapter(String name) throws BeaconException {
        AdapterConfig adapterConfig = dao.find(name);

        BeaconAdapter adapter = createAdapterInstance(adapterConfig);
        adapters.put(adapterConfig.getName(), adapter);

        return adapter;
    }

    /**
     * Create a new instance of the BeaconAdapter using the AdapterConfig values
     *
     * @param adapterConfig Configuration for the values
     * @return new BeaconAdapter instance
     * @throws BeaconException
     */
    private BeaconAdapter createAdapterInstance(AdapterConfig adapterConfig) throws BeaconException {

        Class<BeaconAdapter> adapterClass = adapterConfig.convertAapterClassStringToClass();
        try {
            BeaconAdapter adapter = adapterClass.newInstance();
            adapter.initAdapter(adapterConfig);
            return adapter;
        } catch (Exception e) {
            BeaconException exception = new BeaconException(e.getMessage());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }

}
