package com.directbooks.bankingservice.domain

import io.circe.derivation.{Configuration, ConfiguredCodec}


case class TransactionPayload
(
  accountId: String,
  amount: Double,
  description: String
)derives ConfiguredCodec

object TransactionPayload {
  given Configuration = Configuration.default.withSnakeCaseMemberNames
}


