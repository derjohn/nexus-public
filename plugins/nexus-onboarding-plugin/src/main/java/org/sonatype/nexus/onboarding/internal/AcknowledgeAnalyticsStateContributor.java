/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.onboarding.internal;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.capability.CapabilityReferenceFilterBuilder.CapabilityReferenceFilter;
import org.sonatype.nexus.capability.CapabilityRegistry;
import org.sonatype.nexus.capability.CapabilityType;
import org.sonatype.nexus.common.app.ApplicationVersion;
import org.sonatype.nexus.rapture.StateContributor;

import static org.sonatype.nexus.capability.CapabilityType.capabilityType;

/**
 * State contributor defining whether acknowledgement of the analytics submission state is required.
 *
 * @since 3.next
 */
@Named
@Singleton
public class AcknowledgeAnalyticsStateContributor
    implements StateContributor
{
  protected static final String OSS = "OSS";

  private static final String ANALYTICS_CONFIGURATION = "analytics-configuration";

  private final ApplicationVersion applicationVersion;

  private final CapabilityRegistry capabilityRegistry;

  @Inject
  public AcknowledgeAnalyticsStateContributor(
      final ApplicationVersion applicationVersion,
      final CapabilityRegistry capabilityRegistry)
  {

    this.applicationVersion = applicationVersion;
    this.capabilityRegistry = capabilityRegistry;
  }

  @Nullable
  @Override
  public Map<String, Object> getState() {
    Map<String, Object> properties = new HashMap<>();

    properties.put("acknowledgeAnalytics.required", applies());

    return properties;
  }

  private boolean applies() {
    return OSS.equals(applicationVersion.getEdition()) && analyticsCapabilityAbsent();
  }

  private boolean analyticsCapabilityAbsent() {
    CapabilityType capabilityType = capabilityType(ANALYTICS_CONFIGURATION);
    return capabilityRegistry.get(new CapabilityReferenceFilter().withType(capabilityType).includeNotExposed())
        .isEmpty();
  }
}
