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
package com.dnastack.beacon.beaconizer;

import com.dnastack.beacon.beaconizer.dao.api.BeaconizerDao;
import com.dnastack.beacon.beaconizer.dao.impl.BeaconizerDaoImpl;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import org.ga4gh.beacon.Beacon;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconAlleleResponse;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URISyntaxException;
import java.net.URL;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.not;

/**
 * Rest-Tests for Beaconizer using the SampleBeaconAdapter by default
 *
 * @author patmagee
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BeaconizerTest extends BaseTest {

    @ArquillianResource
    private URL url;

    private static BeaconizerDao dao;

    @BeforeClass
    public static void setUpClass() {
        BeaconizerDaoImpl daoImpl = new BeaconizerDaoImpl();
        daoImpl.init();
        dao = daoImpl;
    }

    private Beacon getBeacon() {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .get(url)
                .then()
                .extract()
                .as(Beacon[].class, ObjectMapperType.GSON)[0];
    }

    /**
     * Ensure that posts to the beacon endpoint are not supported
     */
    @Test
    public void testPostBeaconsNotSupported() {
        given().accept(ContentType.JSON).post(url).then().assertThat().statusCode(not(200));

    }

    /**
     * Ensure that deletes to the beacon endpoint are not supported
     */
    @Test
    public void testDeleteBeaconsNotSupported() {
        given().accept(ContentType.JSON).delete(url).then().assertThat().statusCode(not(200));
    }

    /**
     * Ensure that puts to the beacon endpoint are not supported
     */
    @Test
    public void testPutBeaconsNotSupported() {
        given().accept(ContentType.JSON).put(url).then().assertThat().statusCode(not(200));
    }


    @Test
    public void testGetBeacons() {
        Beacon[] beacons = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .get(url)
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(not(emptyArray()))
                .extract()
                .as(Beacon[].class, ObjectMapperType.GSON);

        for (Beacon beacon : beacons) {
            assertThat(beacon).isNotNull();
            assertThat(beacon.getId()).isNotNull();
            assertThat(beacon.getApiVersion()).isEqualTo("0.3.0");
            assertThat(beacon.getOrganization()).isNotNull();
            assertThat(beacon.getSampleAlleleRequests()).isNotNull();
            assertThat(beacon.getDatasets()).isNotEmpty();
        }
    }

    /**
     * Ensure that posts to the beacon endpoint are not supported
     */
    @Test
    public void testPostBeaconNotSupported() {
        given().accept(ContentType.JSON).post(url).then().assertThat().statusCode(not(200));

    }

    /**
     * Ensure that deletes to the beacon endpoint are not supported
     */
    @Test
    public void testDeleteBeaconNotSupported() {
        given().accept(ContentType.JSON).delete(url).then().assertThat().statusCode(not(200));
    }

    /**
     * Ensure that puts to the beacon endpoint are not supported
     */
    @Test
    public void testPutBeaconNotSupported() {
        given().accept(ContentType.JSON).put(url).then().assertThat().statusCode(not(200));
    }

    @Test
    public void testGetBeacon() {
        Beacon beacon = getBeacon();
        Beacon testBeacon = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .log()
                .all()
                .get(url + beacon.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Beacon.class, ObjectMapperType.GSON);

        assertThat(testBeacon).isEqualToComparingFieldByField(beacon);
    }

    @Test
    public void testGetBeaconError() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .log()
                .all()
                .get(url + "INVALID")
                .then()
                .assertThat()
                .statusCode(404);
    }

    /**
     * Test to make sure that you can get a BeaconAlleleResponse from /query enpoint, and that it complies
     * with the current beacon spec. Uses the sampleAlleleRequest provided by the beacon
     */
    @Test
    public void testGetAllele() throws InterruptedException, URISyntaxException {

        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");

        BeaconAlleleResponse out = given().accept(ContentType.JSON)
                                          .queryParam("referenceName", request.getReferenceName())
                                          .queryParam("start", request.getStart())
                                          .queryParam("referenceBases", request.getReferenceBases())
                                          .queryParam("alternateBases", request.getAlternateBases())
                                          .queryParam("assemblyId", request.getAssemblyId())
                                          .queryParam("datasetIds", request.getDatasetIds())
                                          .queryParam("includeDatasetResponses", request.getIncludeDatasetResponses())
                                          .get(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);

        assertThat(out.getAlleleRequest()).isNotNull();
        assertThat(out.getExists()).isTrue();
        if (request.getIncludeDatasetResponses()) {
            assertThat(out.getDatasetAlleleResponses()).isNotEmpty();
        }
        assertThat(out.getBeaconId()).isEqualTo(beacon.getId());
        assertThat(out.getError()).isNull();
    }

    /**
     * Test to make sure that you can post a BeaconAlleleResponse from /query enpoint, and that it complies
     * with the current beacon spec. Uses the sampleAlleleRequest provided by the beacon
     */
    @Test
    public void testPostAllele() throws URISyntaxException {

        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");

        BeaconAlleleResponse out = given().contentType(ContentType.JSON)
                                          .accept(ContentType.JSON)
                                          .body(request, ObjectMapperType.GSON)
                                          .post(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);

        assertThat(out.getAlleleRequest()).isEqualByComparingTo(request);
        assertThat(out.getExists()).isTrue();
        if (request.getIncludeDatasetResponses()) {
            assertThat(out.getDatasetAlleleResponses()).isNotEmpty();
        }
        assertThat(out.getBeaconId()).isEqualTo(beacon.getId());
        assertThat(out.getError()).isNull();

    }

    /**
     * Test to ensure that Delete is not supported
     */
    @Test
    public void testDeleteAlleleNotSupported() throws URISyntaxException {
        Beacon beacon = getBeacon();
        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");
        given()
                .delete(path)
                .then()
                .assertThat()
                .statusCode(not(200));
    }

    /**
     * Test to ensure that put is not supported
     */
    @Test
    public void testPutAlleleNotSupported() throws URISyntaxException {
        Beacon beacon = getBeacon();
        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");
        given()
                .put(path)
                .then()
                .assertThat()
                .statusCode(not(200));
    }

    /**
     * Test to insure that a post with an invalid request returns a beacon error.
     */
    @Test
    public void testPostInvalidRequest() throws URISyntaxException {
        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");
        request.setReferenceName(null);
        request.setReferenceBases(null);

        BeaconAlleleResponse out = given().accept(ContentType.JSON)
                                          .contentType(ContentType.JSON)
                                          .body(request, ObjectMapperType.GSON)
                                          .post(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);

        assertThat(out.getExists()).isNull();
        assertThat(out.getError()).isNotNull();
        assertThat(out.getError().getErrorCode()).isEqualTo(400);
    }

    /**
     * Test to ensure that a get with missing required params returns a BeaconError
     */
    @Test
    public void testGetAlleleWithMissingRequiredParams() throws URISyntaxException {
        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");

        BeaconAlleleResponse out = given().accept(ContentType.JSON)
                                          .queryParam("referenceBases", request.getReferenceBases())
                                          .queryParam("alternateBases", request.getAlternateBases())
                                          .queryParam("assemblyId", request.getAssemblyId())
                                          .queryParam("datasetIds", request.getDatasetIds())
                                          .queryParam("includeDatasetResponses", request.getIncludeDatasetResponses())
                                          .get(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);

        assertThat(out.getExists()).isNull();
        assertThat(out.getError()).isNotNull();
        assertThat(out.getError().getErrorCode()).isEqualTo(400);
    }

    /**
     * Test to makes sure that we can still get an allele response if optional parameters are not defined
     */
    @Test
    public void testGetAlleleWithMissingOptionalParams() throws URISyntaxException {
        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");
        request.setIncludeDatasetResponses(false);

        BeaconAlleleResponse out = given().accept(ContentType.JSON)
                                          .queryParam("referenceName", request.getReferenceName())
                                          .queryParam("start", request.getStart())
                                          .queryParam("referenceBases", request.getReferenceBases())
                                          .queryParam("alternateBases", request.getAlternateBases())
                                          .queryParam("assemblyId", request.getAssemblyId())
                                          .queryParam("datasetIds", request.getDatasetIds())
                                          .get(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);

        assertThat(out.getAlleleRequest()).isEqualByComparingTo(request);
        assertThat(out.getExists()).isTrue();
        assertThat(out.getDatasetAlleleResponses()).isNullOrEmpty();

    }

    /**
     * Test to ensure that we can retrieve an allele with the datasets listed
     */
    @Test
    public void testGetAlleleWithDataSets() throws URISyntaxException {
        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");

        BeaconAlleleResponse out = given().accept(ContentType.JSON)
                                          .queryParam("referenceName", request.getReferenceName())
                                          .queryParam("start", request.getStart())
                                          .queryParam("referenceBases", request.getReferenceBases())
                                          .queryParam("alternateBases", request.getAlternateBases())
                                          .queryParam("assemblyId", request.getAssemblyId())
                                          .queryParam("datasetIds", request.getDatasetIds())
                                          .queryParam("includeDatasetResponses", request.getIncludeDatasetResponses())
                                          .get(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);
        assertThat(out.getAlleleRequest()).isEqualByComparingTo(request);
        assertThat(out.getExists()).isTrue();
        assertThat(out.getDatasetAlleleResponses()).isNotNull();
        assertThat(out.getDatasetAlleleResponses()).isNotEmpty();

    }

    /**
     * Test to ensure that we can retrieve an allele without the datasets listed
     */
    @Test
    public void testGetAlleleWithoutDatasets() throws URISyntaxException {
        Beacon beacon = getBeacon();
        BeaconAlleleRequest request = beacon.getSampleAlleleRequests().get(0);

        String path = url.toURI().getRawPath().concat(beacon.getId() + "/query");
        request.setIncludeDatasetResponses(false);

        BeaconAlleleResponse out = given().accept(ContentType.JSON)
                                          .queryParam("referenceName", request.getReferenceName())
                                          .queryParam("start", request.getStart())
                                          .queryParam("referenceBases", request.getReferenceBases())
                                          .queryParam("alternateBases", request.getAlternateBases())
                                          .queryParam("assemblyId", request.getAssemblyId())
                                          .queryParam("datasetIds", request.getDatasetIds())
                                          .queryParam("includeDatasetResponses", false)
                                          .get(path)
                                          .then()
                                          .extract()
                                          .as(BeaconAlleleResponse.class, ObjectMapperType.GSON);

        assertThat(out.getAlleleRequest()).isEqualByComparingTo(request);
        assertThat(out.getExists()).isTrue();
        assertThat(out.getDatasetAlleleResponses()).isNullOrEmpty();
        assertThat(out.getDatasetAlleleResponses()).isNullOrEmpty();
    }
}
