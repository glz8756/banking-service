package com.directbooks.bankingservice.http

import cats.*
import cats.effect.*
import cats.syntax.all.*
import io.circe.generic.auto.*
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.*

import com.directbooks.bankingservice.core.*
import com.directbooks.bankingservice.domain.*


class BankingService[F[_] : Concurrent] private
(
  aRepo: AccountRepository[F],
  tRepo: TransactionRepository[F]
) extends Http4sDsl[F] {
  private val prefix = "/api"

  private val accountRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "account" / accid =>
      for {
        account <- aRepo.find(accid)
        accRes = account match {
          case Some(a) =>
            for {
              ad <- aRepo.getAccountDetail(a.accountId)
              adRes = ad match {
                case Some(adt) => Ok(adt.asJson)
                case None => NotFound(s"no account found for account_id(${accid})")
              }
              atresp <- adRes
            } yield atresp
          case None => NotFound(s"no account found for account_id(${accid})")
        }
        resp <- accRes
      } yield resp
  }

  private val transactionRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "transaction" =>
      req.as[TransactionPayload].redeemWith(
        error => BadRequest(s"${error.getMessage}"),
        trload =>
          for {
            account <- aRepo.find(trload.accountId)
            trRes = account match {
              case Some(a) =>
                if (a.balance + trload.amount < 0) UnprocessableEntity("insufficient funds")
                else {
                  for {
                    trid <- aRepo.update(a.accountId, trload.amount, trload.description)
                    otr <- tRepo.findTransactionById(trid)
                    trReps = otr match {
                      case Some(tr) => Created(tr.asJson)
                      case None => NotFound(s"Error!!! there is no transaction been created!")
                    }
                    trCreps <- trReps
                  } yield trCreps
                }
              case None => NotFound(s"no account found for account_id(${trload.accountId})")
            }
            resp <- trRes
          } yield resp
      )
    case GET -> Root / "transaction" / "history" / accid =>
      for {
        allTrs <- tRepo.findAllTransactionById(accid)
        allRes = if (allTrs.size == 0) NotFound(s"no account found for account_id(${accid})") else Ok(allTrs.asJson)
        resp <-  allRes
      } yield resp
  }

  val routes: HttpRoutes[F] = Router(
     prefix -> (accountRoute <+> transactionRoute)
  )
}


object BankingService {
  def resource[F[_] : Concurrent]
  (aRepo: AccountRepository[F],
   tRepo: TransactionRepository[F]
  ): Resource[F, BankingService[F]] =
    Resource.pure(new BankingService[F](aRepo, tRepo))

  def make[F[_] : Concurrent]
  (aRepo: AccountRepository[F],
   tRepo: TransactionRepository[F]
  ): F[BankingService[F]] =
    new BankingService[F](aRepo, tRepo).pure[F]
}
