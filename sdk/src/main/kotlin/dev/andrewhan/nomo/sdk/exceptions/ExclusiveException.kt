package dev.andrewhan.nomo.sdk.exceptions

class ExclusiveException(target: Any, binding: Any) :
  Exception("Target (${target}) already has $binding bound")
