package fr.centralesupelec.db

enum class PositionVerification {
    NONE,
    IDENTIFIED,
    IDENTIFIED_CONTINUOUS,
    ANONYMOUS,
    ANONYMOUS_CONTINUOUS
}

data class CourseModel(val name: String, val teacher: String, val room: String, val time: String, val verification: PositionVerification)