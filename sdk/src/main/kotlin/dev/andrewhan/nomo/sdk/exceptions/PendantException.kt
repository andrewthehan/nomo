package dev.andrewhan.nomo.sdk.exceptions

class PendantException(target: Any, neighbor: Any) :
  Exception("Target (${target}) already bound to $neighbor")
