package examples

class T1 : Thread() {
    override fun run() {
        val reader = LHReader(1)
        reader.start()
    }
}