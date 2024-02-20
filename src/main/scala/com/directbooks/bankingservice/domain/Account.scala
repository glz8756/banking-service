package com.directbooks.bankingservice.domain

import io.circe.derivation.{Configuration, ConfiguredCodec}

case class Account
(
  accountId: String,
  customerId: String,
  balance: Double = 0.00
)derives ConfiguredCodec


object Account {
  given Configuration = Configuration.default.withSnakeCaseMemberNames
}




