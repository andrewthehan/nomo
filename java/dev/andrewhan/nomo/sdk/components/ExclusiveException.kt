package dev.andrewhan.nomo.sdk.components

class ExclusiveException(target: Any, binding: Any) :
  Exception("Target (${target}) already has $binding bound")
