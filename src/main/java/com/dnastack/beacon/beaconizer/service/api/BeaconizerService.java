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
package com.dnastack.beacon.beaconizer.service.api;

import com.dnastack.beacon.exceptions.BeaconException;
import org.ga4gh.beacon.Beacon;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconAlleleResponse;

import java.util.List;

/**
 * BeaconService interface
 *
 * @author patmagee
 */
public interface BeaconizerService {

    /**
     * Get a list of all the beacons
     * @return
     * @throws BeaconException
     */
    List<Beacon> getBeacons() throws BeaconException;

    /**
     * Get information from a specific beacon given the name it was registered under.
     *
     * @param name name of beacon
     * @return Response Object
     */
    Beacon getBeacon(String name) throws BeaconException;


    /**
     * Query a single Beacon for the existence of a variant
     *
     * @param name    name of beacon
     * @param request request object
     * @return Response object
     */
    BeaconAlleleResponse getBeaconAlleleResponse(String name, BeaconAlleleRequest request) throws BeaconException;

    /**
     * Query a single Beacon for the existence of a variant
     *
     * @param name                    name of beacon
     * @param referenceName           name of the reference
     * @param start                   start position
     * @param referenceBases          reference bases
     * @param alternateBases          alternate bases
     * @param assemblyId              genome assembly
     * @param datasetIds              list of datasetIds
     * @param includeDatasetResponses include
     * @return Response Object
     */
    BeaconAlleleResponse getBeaconAlleleResponse(String name, String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException;
}
