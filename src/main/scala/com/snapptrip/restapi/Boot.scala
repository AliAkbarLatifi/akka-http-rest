package com.snapptrip.restapi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.snapptrip.restapi.core.auth.{AuthService, JdbcAuthDataStorage}
import com.snapptrip.restapi.core.profiles.{JdbcUserProfileStorage, UserProfileService}
import com.snapptrip.restapi.http.HttpRoute
import com.snapptrip.restapi.utils.Config
import com.snapptrip.restapi.utils.db.{DatabaseConnector, DatabaseMigrationManager}

import scala.concurrent.ExecutionContext

object Boot extends App {

  def startApplication() = {
    implicit val actorSystem : ActorSystem       = ActorSystem()
    implicit val executor: ExecutionContext      = actorSystem.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = Config.load()

    new DatabaseMigrationManager(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    ).migrateDatabaseSchema()

    val databaseConnector = new DatabaseConnector(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    )

    val userProfileStorage = new JdbcUserProfileStorage(databaseConnector)
    val authDataStorage    = new JdbcAuthDataStorage(databaseConnector)

    val usersService = new UserProfileService(userProfileStorage)
    val authService  = new AuthService(authDataStorage, config.secretKey)
    val httpRoute    = new HttpRoute(usersService, authService, config.secretKey)

    Http().bindAndHandle(httpRoute.route, config.http.host, config.http.port)
  }

  startApplication()

}
