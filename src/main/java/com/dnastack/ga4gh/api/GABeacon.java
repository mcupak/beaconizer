package com.dnastack.ga4gh.api;

/**
 * Beacon implementation
 *
 * @author mfiume
 */
public interface GABeacon {

    /**
     * Whether or not the beacon contains this variant
     *
     * @param genome    The genome build
     * @param reference The chromosome
     * @param position  Coordinate
     * @param alt       The alternate allele
     * @return Whether or not the beacon contains this variant
     */
    Boolean exists(String genome, String reference, long position, String alt);

}
