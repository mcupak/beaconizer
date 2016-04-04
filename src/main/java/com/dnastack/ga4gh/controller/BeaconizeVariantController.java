/*
 * Put license here.
 */
package com.dnastack.ga4gh.controller;

import com.dnastack.ga4gh.dto.BeaconResponseDTO;
import com.dnastack.ga4gh.dto.BeaconResponseListDTO;
import com.dnastack.ga4gh.impl.BeaconizeVariantImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;

/**
 * @author jim
 */
@Path("/")
public class BeaconizeVariantController {

    private static final String API_KEY = "AIzaSyB-RQ8ZfJZO1anOfbyZEaIBYRKvEpXm4iw";

    private static final BeaconizeVariantImpl[] GABeacons = new BeaconizeVariantImpl[]{new BeaconizeVariantImpl("platinum", "https://www.googleapis.com/genomics/v1beta2", API_KEY, new String[]{"3049512673186936334"}), new BeaconizeVariantImpl("thousandgenomes", "https://www.googleapis.com/genomics/v1beta2", API_KEY, new String[]{"4252737135923902652"}), new BeaconizeVariantImpl("thousandgenomes-phase3", "https://www.googleapis.com/genomics/v1beta2", API_KEY, new String[]{"10473108253681171589"}), new BeaconizeVariantImpl("curoverse", "http://lightning-dev4.curoverse.com/api", null, new String[]{"hu"}), new BeaconizeVariantImpl("curoverse-ref", "http://lightning-dev4.curoverse.com/apiref", null, new String[]{"1000g_2013"})};

    /**
     * Returns whether the variant exists in the Variant API Implementation with
     * name 'requestedName'. If no beacon with the name 'name' exists, returns 404 not
     * found.
     *
     * @param requestedName    - The name of the Variant API Implementation to query.
     * @param populationId     - The population Id
     * @param referenceVersion - The reference version
     * @param chromosome       - The chromosome of the variant to query
     * @param coordinate       - The coordinate of the variant to query.
     * @param allele           - The allele of the variant to query.
     * @return 404 not found if the requestedName is not found, otherwise a BeaconResponseDTO that records whether or
     * not the variant exists.
     */
    @GET
    @Path("{name}")
    @Produces({"application/xml", "application/json"})
    public Response find(@Context UriInfo uriInfo, @PathParam("name") String requestedName, @QueryParam("populationId") String populationId, @QueryParam("referenceVersion") String referenceVersion, @QueryParam("chromosome") String chromosome, @QueryParam("coordinate") Long coordinate, @QueryParam("allele") String allele) {

        for (BeaconizeVariantImpl gaBeacon : GABeacons) {
            if (gaBeacon.getName().equals(requestedName)) {
                if (gaBeacon.exists(null, chromosome, coordinate, allele)) {
                    return Response.ok(new BeaconResponseDTO(gaBeacon.getName(), true)).build();
                } else {
                    return Response.ok(new BeaconResponseDTO(gaBeacon.getName(), false)).build();
                }
            }
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No Variant API Implementation with the name " + requestedName + " exists in this registry.").build();
    }

    /**
     * Returns a list of beacon responses, one for each variant API implementation registered in the
     * GABeacons field.
     *
     * @param populationId     - The population Id
     * @param referenceVersion - The reference version
     * @param chromosome       - The chromosome of the variant to query
     * @param coordinate       - The coordinate of the variant to query.
     * @param allele           - The allele of the variant to query.
     * @return A list of all beacon responses, one for each Variant API implementation registered in the GABeacons
     * field.
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response findAll(@Context UriInfo uriInfo, @QueryParam("populationId") String populationId, @QueryParam("referenceVersion") String referenceVersion, @QueryParam("chromosome") String chromosome, @QueryParam("coordinate") Long coordinate, @QueryParam("allele") String allele) {
        ArrayList<BeaconResponseDTO> l = new ArrayList<>(GABeacons.length);
        for (BeaconizeVariantImpl gaBeacon : GABeacons) {
            //localhost:8080/beacon/beacon?chromosome=1&coordinate=10177&allele=AC
            Boolean doesExist = gaBeacon.exists(null, chromosome, coordinate, allele);
            l.add(new BeaconResponseDTO(gaBeacon.getName(), doesExist));
        }
        BeaconResponseListDTO b = new BeaconResponseListDTO();
        b.setBeaconResponses(l);
        return Response.ok().entity(b).build();
    }

}
