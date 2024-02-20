package com.directbooks.bankingservice.core


import cats.effect.*
import cats.syntax.all.*
import cats.implicits.*

import scala.collection.mutable
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util as ju
import doobie.util.{ExecutionContexts, transactor}
import doobie.hikari.HikariTransactor
import com.directbooks.bankingservice.domain.*
import com.directbooks.bankingservice.utils.*
import com.directbooks.bankingservice.utils.Utils.*
import java.util.UUID


trait TransactionRepository[F[_]]{
  def findTransactionById(id: String): F[Option[Transaction]]
  def findAllTransactionById(aid: String): F[List[Transaction]]
}

 class TransactionRepositoryLive[F[_]: Concurrent]  private (xa: Transactor[F])
  extends TransactionRepository[F] {
  override def findAllTransactionById(aid: String): F[List[Transaction]] = {
    sql"""
    select transaction_id, account_id, amount, description, transaction_date
    from transaction where account_id = $aid
    order by transaction_date desc
  """.query[Transaction]
     .to[List]
     .transact(xa)
  }

   override def findTransactionById(id: String): F[Option[Transaction]] = {
     sql"""
     select transaction_id, account_id, amount, description, transaction_date
     from transaction where transaction_id = $id;
   """.query[Transaction]
       .option
       .transact(xa)
   }
 }

object TransactionRepositoryLive{
  def make[F[_] : Concurrent](xa: Transactor[F]): F[TransactionRepositoryLive[F]] =
   new TransactionRepositoryLive(xa).pure[F]

  def resource[F[_] : Concurrent](xa: Transactor[F]): Resource[F, TransactionRepositoryLive[F]] = {
    Resource.pure(new TransactionRepositoryLive(xa))
  }
}








