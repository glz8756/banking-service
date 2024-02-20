package com.directbooks.bankingservice

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.{host, port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

import com.directbooks.bankingservice.core.*
import com.directbooks.bankingservice.http.BankingService


object BankingHttpService extends IOApp.Simple {

  def makePostgres = for {
    ec <- ExecutionContexts.fixedThreadPool[IO](32)
    transactor <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost:5432/db_bank",
      "docker",
      "docker",
      ec
    )
  } yield transactor

  def makeServer: Resource[IO, Server] = for {
    postgres <- makePostgres
    aRepo <- AccountRepositoryLive.resource[IO](postgres)
    tRepo <- TransactionRepositoryLive.resource[IO](postgres)
    bankingServiceApi <- BankingService.resource[IO](aRepo, tRepo)
    server <- EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8088")
      .withHttpApp(bankingServiceApi.routes.orNotFound)
      .build
  } yield server

  override def run: IO[Unit] =
    makeServer.use(_ => IO.println("Banking Service Server is ready!") *> IO.never)
}
