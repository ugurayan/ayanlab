package com.ayanlab.comm.backend.event;

import java.util.UUID;

public record CommEmailDispatchEvent(
    UUID outboxId,
    UUID campaignId,
    String recipientEmail
) {}
