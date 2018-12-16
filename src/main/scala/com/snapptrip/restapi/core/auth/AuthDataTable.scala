package com.snapptrip.restapi.core.auth

import com.snapptrip.restapi.core.AuthData
import com.snapptrip.restapi.utils.db.DatabaseConnector

private[auth] trait AuthDataTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class AuthDataSchema(tag: Tag) extends Table[AuthData](tag, "auth") {
    def id       = column[String]("id", O.PrimaryKey)
    def username = column[String]("username")
    def email    = column[String]("email")
    def password = column[String]("password")

    def * = (id, username, email, password) <> ((AuthData.apply _).tupled, AuthData.unapply)
  }

  protected val auth = TableQuery[AuthDataSchema]

}