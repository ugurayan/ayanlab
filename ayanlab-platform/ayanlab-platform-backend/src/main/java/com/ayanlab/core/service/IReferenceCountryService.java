package com.ayanlab.core.service;

import com.ayanlab.core.entity.ReferenceCountry;

import java.util.List;

public interface IReferenceCountryService {

    List<ReferenceCountry> listAll();

    ReferenceCountry findByAlpha2(String alpha2);

    List<ReferenceCountry> search(String query);

    List<ReferenceCountry> listByRegion(String region);
}