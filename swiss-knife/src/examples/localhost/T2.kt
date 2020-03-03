package examples.localhost

class T2 : Thread() {
    override fun run() {
        val tag = LHTag(10)
        tag.start()
    }
}