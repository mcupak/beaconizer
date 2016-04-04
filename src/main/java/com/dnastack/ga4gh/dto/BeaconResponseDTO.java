/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dnastack.ga4gh.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jim
 */
@XmlRootElement(name = "BEACONResponse")
public class BeaconResponseDTO {

    private String name;
    private Boolean exists;
    private Long alleleFreq;

    public BeaconResponseDTO() {

    }

    public BeaconResponseDTO(String name, Boolean exists) {
        this.exists = exists;
        this.alleleFreq = null;
    }

    public BeaconResponseDTO(String name, Boolean exists, Long alleleFreq) {
        this.exists = exists;
        this.alleleFreq = alleleFreq;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "exists")
    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    @XmlElement(name = "frequency")
    public Long getAlleleFreq() {
        return alleleFreq;
    }

    public void setAlleleFreq(Long alleleFreq) {
        this.alleleFreq = alleleFreq;
    }
}
