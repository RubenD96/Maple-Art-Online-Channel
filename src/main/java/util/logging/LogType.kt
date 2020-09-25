package util.logging

enum class LogType(val console: Boolean) {
    HACK(true),
    NULL(true),
    INVALID(true), // if data is invalid, but not null, and hack is not guaranteed
    MISSING(false), // missing data such as a script not existing
    BLOCK(false), // user is blocked from performing a certain action
    UNCODED(true),
    MISC_CONSOLE(true),
    MISC_NO_CONSOLE(false)
}