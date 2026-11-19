package com.ayanlab.pms.backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpmsOpenApiConfiguration {

    @Bean
    public GroupedOpenApi ipmsRentalApis() {
        return GroupedOpenApi.builder()
                .group("01-rental")
                .pathsToMatch(
                        "/pms/api/properties/**",
                        "/pms/api/property-units/**",
                        "/pms/api/tenants/**",
                        "/pms/api/leases/**",
                        "/pms/api/commercial-lease-terms/**",
                        "/pms/api/rental-owners/**",
                        "/pms/api/outstanding-balances/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ipmsAssociationApis() {
        return GroupedOpenApi.builder()
                .group("02-association")
                .pathsToMatch(
                        "/pms/api/associations/**",
                        "/pms/api/violations/**",
                        "/pms/api/architectural-requests/**",
                        "/pms/api/ownership-accounts/**",
                        "/pms/api/cooperative-share-records/**",
                        "/pms/api/coop-board-packets/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ipmsAccountingApis() {
        return GroupedOpenApi.builder()
                .group("03-accounting")
                .pathsToMatch(
                        "/pms/api/chart-of-accounts/**",
                        "/pms/api/transactions/**",
                        "/pms/api/bank-accounts/**",
                        "/pms/api/bills/**",
                        "/pms/api/budgets/**",
                        "/pms/api/payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ipmsTrApis() {
        return GroupedOpenApi.builder()
                .group("04-tr-modules")
                .pathsToMatch(
                        "/pms/api/late-penalty-rules/**",
                        "/pms/api/meter-readings/**",
                        "/pms/api/enforcement-cases/**",
                        "/pms/api/debt-inquiry-codes/**",
                        "/pms/api/parcel-deliveries/**",
                        "/pms/api/patrol-routes/**",
                        "/pms/api/access-control-events/**",
                        "/pms/api/staff-timesheets/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ipmsIntegrationApis() {
        return GroupedOpenApi.builder()
                .group("05-integrations")
                .pathsToMatch(
                        "/pms/api/webhooks/**",
                        "/pms/api/shortcuts/**",
                        "/pms/api/flows/**",
                        "/pms/api/marketplace/**")
                .build();
    }
}