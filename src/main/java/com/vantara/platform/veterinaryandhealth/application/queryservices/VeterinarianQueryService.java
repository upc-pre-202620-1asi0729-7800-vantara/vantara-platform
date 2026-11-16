
package com.vantara.platform.veterinaryandhealth.application.queryservices;

import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VeterinarianResource;

import java.util.List;

public interface VeterinarianQueryService {

    List<VeterinarianResource> getAllVeterinarians();

}
