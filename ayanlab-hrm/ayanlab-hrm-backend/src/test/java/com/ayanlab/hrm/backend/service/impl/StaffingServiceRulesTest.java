package com.ayanlab.hrm.backend.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.hrm.backend.entity.Brand;
import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.hrm.backend.enums.ReferenceEntityKind;
import com.ayanlab.hrm.backend.repository.BrandRepository;
import com.ayanlab.hrm.backend.repository.LegalEntityRepository;
import com.ayanlab.hrm.backend.repository.LocationRepository;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.repository.ShiftTemplateRepository;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class StaffingServiceRulesTest {

    private final OrganizationRepository organizations = mock(OrganizationRepository.class);
    private final LegalEntityRepository legalEntities = mock(LegalEntityRepository.class);
    private final BrandRepository brands = mock(BrandRepository.class);
    private final LocationRepository locations = mock(LocationRepository.class);
    private final ShiftTemplateRepository templates = mock(ShiftTemplateRepository.class);
    private final ShiftRepository shifts = mock(ShiftRepository.class);
    private final HrmPlatformAuthorizationService authorization = mock(HrmPlatformAuthorizationService.class);

    private OrganizationServiceImpl organizationsService;
    private BrandServiceImpl brandService;
    private LocationServiceImpl locationService;

    @BeforeEach
    void setUp() {
        organizationsService = new OrganizationServiceImpl(
                organizations, legalEntities, brands, locations, templates, shifts, authorization);
        brandService = new BrandServiceImpl(brands, organizations, locations, authorization);
        locationService = new LocationServiceImpl(
                locations, organizations, brands, legalEntities, shifts, templates, authorization);
        when(locations.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void secondProfileForTheSameOrganizationIsRejected() {
        UUID orgId = UUID.randomUUID();
        when(organizations.findByPlatformOrganizationId(orgId)).thenReturn(Optional.of(profile(orgId)));

        assertThatThrownBy(() -> organizationsService.create(profile(orgId), context(null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void profileDeleteIsRejectedWhenABrandExists() {
        UUID orgId = UUID.randomUUID();
        Organization profile = profile(orgId);
        profile.setId(UUID.randomUUID());
        when(organizations.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(legalEntities.findByPlatformOrganizationId(orgId)).thenReturn(List.of());
        Brand brand = new Brand();
        brand.setPlatformOrganizationId(orgId);
        when(brands.findByPlatformOrganizationId(orgId)).thenReturn(List.of(brand));

        assertThatThrownBy(() -> organizationsService.deleteById(profile.getId(), context(null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("still has");
    }

    @Test
    void brandDeleteIsRejectedWhenALocationExists() {
        Brand brand = new Brand();
        brand.setId(UUID.randomUUID());
        brand.setName("Acme");
        brand.setArea("Zurich");
        brand.setPlatformOrganizationId(UUID.randomUUID());
        when(brands.findById(brand.getId())).thenReturn(Optional.of(brand));
        when(locations.existsByReferenceEntityAndReferenceId(ReferenceEntityKind.BRAND, brand.getId())).thenReturn(true);

        assertThatThrownBy(() -> brandService.deleteById(brand.getId(), context(null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Brand still has locations");
    }

    @Test
    void unknownBrandLocationIsRejected() {
        Location location = location(ReferenceEntityKind.BRAND, UUID.randomUUID());
        when(brands.findById(location.getReferenceId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.create(location, context(null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("brand does not exist");
    }

    @Test
    void userReferenceLocationIsSavedWithoutAnExistenceCheck() {
        UUID userId = UUID.randomUUID();
        Location location = location(ReferenceEntityKind.USER, userId);

        Location saved = locationService.create(location, context(null));

        assertThat(saved.getReferenceEntity()).isEqualTo(ReferenceEntityKind.USER);
        assertThat(saved.getReferenceId()).isEqualTo(userId);
        assertThat(saved.getId()).isNotNull();
    }

    private static Organization profile(UUID orgId) {
        Organization organization = new Organization();
        organization.setPlatformOrganizationId(orgId);
        organization.setName("Acme");
        organization.setEmail("ops@acme.example");
        organization.setPhone("+41000000000");
        organization.setTimezone("Europe/Zurich");
        organization.setCurrency("CHF");
        return organization;
    }

    private static Location location(ReferenceEntityKind kind, UUID referenceId) {
        Location location = new Location();
        location.setReferenceEntity(kind);
        location.setReferenceId(referenceId);
        location.setName("Front desk");
        location.setFormattedAddress("1 Paradeplatz, Zurich");
        return location;
    }

    private static CoreContext context(UUID orgId) {
        CoreContext context = new CoreContext();
        context.setOrgId(orgId);
        context.setUserId(UUID.randomUUID());
        return context;
    }
}
