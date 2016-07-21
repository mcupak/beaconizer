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
package com.dnastack.beacon.beaconizer.service.impl;

import com.dnastack.beacon.adapter.api.BeaconAdapter;
import com.dnastack.beacon.beaconizer.service.api.BeaconizerService;
import com.dnastack.beacon.beaconizer.util.BeaconAdapterFactory;
import com.dnastack.beacon.exceptions.BeaconAlleleRequestException;
import com.dnastack.beacon.exceptions.BeaconException;
import com.dnastack.beacon.utils.Reason;
import org.ga4gh.beacon.Beacon;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconAlleleResponse;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * BeaconService Implementation
 *
 * @author patmagee
 */
@Singleton
public class BeaconizerServiceImpl implements BeaconizerService {

    @Inject
    BeaconAdapterFactory beaconAdapterFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Beacon> getBeacons() throws BeaconException {
        List<String> beaconNames = beaconAdapterFactory.listRegisteredBeacons();
        List<Beacon> beacons = new ArrayList<>();

        for (String beaconName : beaconNames) {
            beacons.add(beaconAdapterFactory.getAdapter(beaconName).getBeacon());
        }
        return beacons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Beacon getBeacon(String name) throws BeaconException {
        return beaconAdapterFactory.getAdapter(name).getBeacon();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BeaconAlleleResponse getBeaconAlleleResponse(String name, String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException {
        BeaconAdapter adapter = beaconAdapterFactory.getAdapter(name);
        validateRequest(name, referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds, includeDatasetResponses);
        return adapter
                .getBeaconAlleleResponse(referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds, includeDatasetResponses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BeaconAlleleResponse getBeaconAlleleResponse(String name, BeaconAlleleRequest request) throws BeaconException {
        BeaconAdapter adapter = beaconAdapterFactory.getAdapter(name);
        validateRequest(name, request.getReferenceName(), request.getStart(), request.getReferenceBases(), request.getAlternateBases(), request
                .getAssemblyId(), request.getDatasetIds(), request.getIncludeDatasetResponses());
        return adapter.getBeaconAlleleResponse(request);
    }


    /**
     * Validate the beacon fields according to the 0.3.0 beacon specifications
     *
     * @param referenceName
     * @param start
     * @param referenceBases
     * @param alternateBases
     * @param assemblyId
     * @throws BeaconException
     */
    private void validateRequest(String name, String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconAlleleRequestException {
        if (referenceName == null) {
            throw new BeaconAlleleRequestException(Reason.INVALID_REQUEST, "Reference cannot be null. Please provide an appropriate reference name");
        } else if (start == null) {
            throw new BeaconAlleleRequestException(Reason.INVALID_REQUEST, "Start position cannot be null. Please provide a 0-based start position");
        } else if (referenceBases == null) {
            throw new BeaconAlleleRequestException(Reason.INVALID_REQUEST, "Reference bases cannot be null");
        } else if (alternateBases == null) {
            throw new BeaconAlleleRequestException(Reason.INVALID_REQUEST, "Alternate bases cannot be null");
        } else if (assemblyId == null) {
            throw new BeaconAlleleRequestException(Reason.INVALID_REQUEST, "AssemblyId cannot be null. Please defined a valid GRCh assembly Id");
        } else if (datasetIds == null || datasetIds.size() == 0) {
            throw new BeaconAlleleRequestException(Reason.INVALID_REQUEST, "Missing DatasetId. At least 1 dataset id must be provided");
        }
    }


}
