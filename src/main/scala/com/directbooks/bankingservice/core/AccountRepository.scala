package com.directbooks.bankingservice.core


import cats.effect.*
import cats.syntax.all.*
import cats.implicits.*

import scala.collection.mutable
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util as ju
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor

import com.directbooks.bankingservice.domain.*
import com.directbooks.bankingservice.utils.Utils.getUUID

trait AccountRepository[F[_]]{
  def update(accid: String, amount: Double, desc: String):F[String]
  def find(accid: String): F[Option[Account]]
  def getAccountDetail(accid: String): F[Option[AccountDetail]]
}


class AccountRepositoryLive[F[_] : Concurrent] private (xa: Transactor[F])
  extends AccountRepository[F] {

  override def update(accid: String, amount: Double, desc: String): F[String] = {
    val transactionId = getUUID().toString
    sql"""
         begin;
           insert into transaction(transaction_id, account_id, amount, description, transaction_date)
             values($transactionId, $accid, $amount, $desc, now());
           update account set balance = balance + $amount where account_id = $accid;
         commit;
       """
      .update
      .run
      .transact(xa)
      .flatMap(_ => transactionId.pure[F])

  }

  override def find(id: String): F[Option[Account]] = {
    sql"""
          SELECT
          account_id,
          customer_id,
          balance
          FROM account
          where account_id = ${id};
       """
      .query[Account]
      .option
      .transact(xa)
  } 

  def getAccountDetail(aid: String): F[Option[AccountDetail]] = {
    sql"""
          select  a.account_id, c.first_name || ' ' || c.last_name, a.balance
          from customer c join account a on c.id = a.customer_id
          where a.account_id = ${aid};
        """
      .query[AccountDetail]
      .option
      .transact(xa)
  }
}

object AccountRepositoryLive {
  def make[F[_] : Concurrent](xa: Transactor[F]): F[AccountRepositoryLive[F]] =
   new AccountRepositoryLive(xa).pure[F]

  def resource[F[_] : Concurrent](xa: Transactor[F]): Resource[F, AccountRepositoryLive[F]] = {
    Resource.pure( new AccountRepositoryLive(xa))
  }
}










