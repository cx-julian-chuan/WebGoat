/*
 * SPDX-FileCopyrightText: Copyright © 2020 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.integration;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

class ActuatorAccessIntegrationTest extends IntegrationTest {

  @Test
  void healthEndpointIsPublic() {
    // Used by the container HEALTHCHECK, so it must stay reachable without a session.
    RestAssured.given()
        .when()
        .relaxedHTTPSValidation()
        .get(webGoatUrlConfig.url("actuator/health"))
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

  @Test
  void envEndpointRequiresAuthentication() {
    // Anonymous requests are redirected to the login page instead of leaking configuration.
    RestAssured.given()
        .when()
        .redirects()
        .follow(false)
        .relaxedHTTPSValidation()
        .get(webGoatUrlConfig.url("actuator/env"))
        .then()
        .statusCode(HttpStatus.SC_MOVED_TEMPORARILY);
  }

  @Test
  void configPropsEndpointRequiresAuthentication() {
    RestAssured.given()
        .when()
        .redirects()
        .follow(false)
        .relaxedHTTPSValidation()
        .get(webGoatUrlConfig.url("actuator/configprops"))
        .then()
        .statusCode(HttpStatus.SC_MOVED_TEMPORARILY);
  }

  @Test
  void envEndpointForbiddenForNonAdminUser() {
    // A regular authenticated user does not hold WEBGOAT_ADMIN.
    RestAssured.given()
        .when()
        .relaxedHTTPSValidation()
        .cookie("JSESSIONID", getWebGoatCookie())
        .get(webGoatUrlConfig.url("actuator/env"))
        .then()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }
}
