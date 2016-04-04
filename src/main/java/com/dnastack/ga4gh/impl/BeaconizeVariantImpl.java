package com.dnastack.ga4gh.impl;

import com.dnastack.ga4gh.api.GABeacon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * A class which implements a beacon from an implementation of GA4GH Variant API.
 *
 * @author mfiume
 */
public class BeaconizeVariantImpl implements GABeacon {

    // the URL to this implementation of GA Variant API
    // includes /variant/search and key={API_KEY}
    private final String fullVariantSearchURL;

    // the variant set IDs to look in
    private final String[] variantSetIds;

    private final String name;

    /**
     * Constructor
     *
     * @param name             A short name for this API implementation.
     * @param variantSearchURL The root URL of the API. Does NOT include /variant/search or key={API_KEY}
     * @param variantSetIds    The variant set IDs to look in
     */
    public BeaconizeVariantImpl(String name, String variantSearchURL, String[] variantSetIds) {
        this(name, variantSearchURL, null, variantSetIds);
    }

    /**
     * @param name             A short name for this API implementation.
     * @param variantSearchURL The root URL of the API. Does NOT include /variant/search or key={API_KEY}
     * @param key              The API key
     * @param variantSetIds    The variant set IDs to look in
     */
    public BeaconizeVariantImpl(String name, String variantSearchURL, String key, String[] variantSetIds) {
        this.fullVariantSearchURL = variantSearchURL + "/variants/search" + (key == null ? "" : "?key=" + key);
        this.variantSetIds = variantSetIds;
        this.name = name;
    }

    /**
     * @return The name of this API implementation.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Queries the Variant API implementation for variants at the given position
     *
     * @param reference The reference (or chromosome)
     * @param position  Position on the reference
     * @param alt       The alternate allele to match against (currently not supported by GASearchVariantsRequest)
     * @return A JSON Object containing a GASearchVariantsResponse
     * @throws IOException    Problems contacting API
     * @throws ParseException Problems parsing response
     */
    private JSONObject submitVariantSearchRequest(String reference, long position, String alt) throws IOException, ParseException {

        JSONObject obj = new JSONObject();

        JSONArray list = new JSONArray();
        list.addAll(Arrays.asList(variantSetIds));

        obj.put("variantSetIds", list);
        obj.put("referenceName", reference);
        obj.put("start", position);
        obj.put("end", (position + 1));
        //obj.put("maxCalls", "1");

        String json = obj.toJSONString();

        URL url = new URL(fullVariantSearchURL);

        StringBuilder postData = new StringBuilder();
        postData.append(json);

        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        StringBuilder sb = new StringBuilder();

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        for (int c; (c = in.read()) >= 0; ) {
            sb.append((char) c);
        }

        String jsonString = sb.toString();

        return (JSONObject) JSONValue.parseWithException(jsonString);
    }

    /**
     * Parse the JSON result and look for variants having the same
     * alt as specified.
     *
     * @param obj The JSON reponse
     * @param alt The alt to look for
     * @return Whether or not any variant contains the alt as an alternate base
     * @throws ParseException Problems parsing the JSON
     */
    private boolean parseResponseForMatchWithAlt(JSONObject obj, String alt) throws ParseException {

        if (obj.isEmpty()) {
            return false;
        }

        JSONArray vars = (JSONArray) obj.get("variants");

        for (Object var : vars) {
            JSONArray bases = (JSONArray) ((JSONObject) var).get("alternateBases");
            if (bases == null) {
                continue;
            }
            for (Object base : bases) {
                if (base.toString().equals(alt)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Boolean exists(String genome, String reference, long position, String alt) {
        try {
            // TODO: check inputs!
            JSONObject response = submitVariantSearchRequest(reference, position, alt);
            return parseResponseForMatchWithAlt(response, alt);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
