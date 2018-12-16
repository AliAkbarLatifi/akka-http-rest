package com.snapptrip.http

import akka.http.scaladsl.server.Route
import com.snapptrip.BaseServiceTest
import com.snapptrip.restapi.core.auth.AuthService
import com.snapptrip.restapi.core.profiles.UserProfileService
import com.snapptrip.restapi.http.HttpRoute

class HttpRouteTest extends BaseServiceTest {

  "HttpRoute" when {

    "GET /healthcheck" should {

      "return 200 OK" in new Context {
        Get("/healthcheck") ~> httpRoute ~> check {
          responseAs[String] shouldBe "OK"
          status.intValue() shouldBe 200
        }
      }

    }

  }

  trait Context {
    val secretKey = "secret"
    val userProfileService: UserProfileService = mock[UserProfileService]
    val authService: AuthService = mock[AuthService]

    val httpRoute: Route = new HttpRoute(userProfileService, authService, secretKey).route
  }

}
