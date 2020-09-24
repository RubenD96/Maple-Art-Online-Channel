package field.obj.reactor

data class ReactorTemplate(val id: Int) {

    var action: String = ""
    val events = ArrayList<ReactorEvent>()
}