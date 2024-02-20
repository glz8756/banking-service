package com.directbooks.bankingservice.domain

import io.circe.syntax.*
import io.circe.derivation.{Configuration, ConfiguredCodec}


case class Transaction
(
  transactionId: String,
  accountId: String,
  amount: Double,
  description: String,
  transactionDate: String
)derives ConfiguredCodec

object Transaction {
  given Configuration = Configuration.default.withSnakeCaseMemberNames
}



