import java.io.File
import java.util.Properties
import java.io.FileInputStream

object myLocalSecret {
    private val properties = Properties()
    init {
        val localPropertiesFile = File("local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { fis ->
                properties.load(fis)
            }
        } else {
            println("Warning: local.properties file not found")
        }
    }
    fun getProperty(key: String): String? {
        val value = properties.getProperty(key)
        println("Property $key = $value")
        return value
    }
}