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
package com.dnastack.beacon.beaconizer.rest.impl;

import com.dnastack.beacon.beaconizer.exceptions.BeaconException;
import com.dnastack.beacon.beaconizer.exceptions.BeaconNotFoundException;
import com.dnastack.beacon.beaconizer.rest.api.Beaconizer;
import com.dnastack.beacon.beaconizer.service.api.BeaconizerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconError;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Beaconizer REST-API implementation
 * @author patmagee
 */
@Path("/")
public class BeaconizerImpl implements Beaconizer {

    @Inject
    BeaconizerService beaconizerService;

    private Gson gson = new GsonBuilder().create();

    /**
     * {@inheritDoc}
     */
    @Override
    public Response listBeacons() {
        try {
            return Response.ok(beaconizerService.getBeacons()).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response searchAllBeacons(String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) {
        try {
            return Response
                    .ok(beaconizerService.getAllBeaconAlleleResponses(referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds, includeDatasetResponses))
                    .build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response searchAllBeacons(BeaconAlleleRequest request) {
        try {
            return Response.ok(beaconizerService.getAllBeaconAlleleResponse(request)).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getBeacon(String name) {
        try {
            return Response.ok(beaconizerService.getBeacon(name)).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getBeaconResponse(String name, String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) {
        try {
            return Response
                    .ok(beaconizerService.getBeaconAlleleResponse(name, referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds, includeDatasetResponses))
                    .build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getBeaconResponse(String name, BeaconAlleleRequest request) {
        try {
            return Response.ok(beaconizerService.getBeaconAlleleResponse(name, request)).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    /**
     * Given a passed BeaconException, form a new beaconError object and return it wrapped in a response object
     * @param e BeaconException
     * @return Response object
     */
    private Response formBeaconError(BeaconException e) {

        String message = e.getMessage();
        try {
            BeaconError error = gson.fromJson(message, BeaconError.class);
            return Response.status(error.getErrorCode()).entity(error).build();
        } catch (Exception ex) {

            BeaconError error = new BeaconError();
            error.setMessage(e.getMessage());

            if (e instanceof BeaconNotFoundException) {
                error.setErrorCode(404);
            } else {
                error.setErrorCode(500);
            }

            return Response.status(error.getErrorCode()).entity(error).build();
        }
    }

}
