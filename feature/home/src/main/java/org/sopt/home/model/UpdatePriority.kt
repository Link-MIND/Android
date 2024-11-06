package org.sopt.home.model

enum class UpdatePriority {
  EMPTY,
  MINOR,
  MAJOR,
  CRITICAL,
  ;

  companion object {
    fun toUpdatePriority(value: Int): UpdatePriority {
      return when (value) {
        in 0..1 -> MINOR
        in 2..3 -> MAJOR
        in 4..5 -> CRITICAL
        else -> EMPTY
      }
    }
  }
}
