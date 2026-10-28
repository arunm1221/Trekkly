package com.example.trekkly.domain.model

enum class DifficultyLevel {
  EASY,MODERATE,DIFFICULT,EXPERT;

    companion object{
        fun fromString(value: String?): DifficultyLevel =
            entries.firstOrNull{it.name.equals(value?.trim(), ignoreCase = true)}?: DifficultyLevel.MODERATE
    }
}