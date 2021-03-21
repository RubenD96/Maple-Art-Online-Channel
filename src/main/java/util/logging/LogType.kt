package util.logging

/**
 * @param console when true, prints the message to the server console
 * @param bulk when false, creates a separate file each log
 */
enum class LogType(val console: Boolean, val bulk: Boolean = false) {
    // errors, hacks etc
    HACK(true),
    NULL(true),
    INVALID(true), // if data is invalid, but not null, and hack is not guaranteed
    MISSING(false), // missing data such as a script not existing
    BLOCK(false), // user is blocked from performing a certain action
    UNCODED(true),
    MISC_CONSOLE(true),
    MISC_NO_CONSOLE(false),

    // info
    PARTY(false, true),
    GUILD(false, true),
    COMMAND(false, true),
    CENTRAL(true, false),
    FIELD_KEY(false, true)
}