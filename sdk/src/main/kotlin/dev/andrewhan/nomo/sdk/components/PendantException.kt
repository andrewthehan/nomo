package dev.andrewhan.nomo.sdk.components

class PendantException(target: Any, neighbor: Any) :
  Exception("Target (${target}) already bound to $neighbor")
