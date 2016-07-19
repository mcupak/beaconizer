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
 * Created by patrickmagee on 2016-07-19.
 */
@Path("/")
public class BeaconizerImpl implements Beaconizer {

    @Inject
    BeaconizerService beaconizerService;

    private Gson gson = new GsonBuilder().create();


    @Override
    public Response listBeacons() {
        try {
            return Response.ok(beaconizerService.getBeacons()).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

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

    @Override
    public Response searchAllBeacons(BeaconAlleleRequest request) {
        try {
            return Response.ok(beaconizerService.getAllBeaconAlleleResponse(request)).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

    @Override
    public Response getBeacon(String name) {
        try {
            return Response.ok(beaconizerService.getBeacon(name)).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

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

    @Override
    public Response getBeaconResponse(String name, BeaconAlleleRequest request) {
        try {
            return Response.ok(beaconizerService.getBeaconAlleleResponse(name, request)).build();
        } catch (BeaconException e) {
            return formBeaconError(e);
        }
    }

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
