package com.hobby.dealabs

import android.content.Context
import android.os.AsyncTask
import android.util.Base64
import java.io.*
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

enum class SaveOperation {
    INSERT, UPDATE, DELETE
}

class SaveToDbTask : AsyncTask<Any, Void, Void>() {
    
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Any): Void? {
        val operation = params[0] as SaveOperation
        val hobby = params[1] as Hobby
        val context = App.instance.applicationContext
        
        try {
            val file = File(context.filesDir, "hobbies.dcrypt")
            val hobbies = mutableListOf<Hobby>()
            
            // Read existing data if file exists
            if (file.exists()) {
                val encryptedData = file.readBytes()
                val decryptedData = decryptData(encryptedData, getAesKey())
                ObjectInputStream(ByteArrayInputStream(decryptedData)).use { ois ->
                    @Suppress("UNCHECKED_CAST")
                    hobbies.addAll(ois.readObject() as List<Hobby>)
                }
            }
            
            // Perform operation
            when (operation) {
                SaveOperation.INSERT -> hobbies.add(hobby)
                SaveOperation.UPDATE -> {
                    val index = hobbies.indexOfFirst { it.id == hobby.id }
                    if (index != -1) {
                        hobbies[index] = hobby
                    }
                }
                SaveOperation.DELETE -> hobbies.removeIf { it.id == hobby.id }
            }
            
            // Save encrypted data
            val byteStream = ByteArrayOutputStream()
            ObjectOutputStream(byteStream).use { oos ->
                oos.writeObject(hobbies)
            }
            val dataToEncrypt = byteStream.toByteArray()
            val encryptedData = encryptData(dataToEncrypt, getAesKey())
            
            FileOutputStream(file).use { fos ->
                fos.write(encryptedData)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return null
    }
    
    private fun getAesKey(): SecretKeySpec {
        // In a real app, use Android Keystore for better security
        val key = "MySuperSecretKey123".toByteArray() // 16, 24, or 32 bytes
        return SecretKeySpec(key, "AES")
    }
    
    private fun encryptData(data: ByteArray, key: SecretKeySpec): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }
    
    private fun decryptData(encryptedData: ByteArray, key: SecretKeySpec): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(encryptedData)
    }
}

class LoadFromDbTask(private val callback: (List<Hobby>) -> Unit) : AsyncTask<Void, Void, List<Hobby>>() {
    
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): List<Hobby> {
        val context = App.instance.applicationContext
        val file = File(context.filesDir, "hobbies.dcrypt")
        
        return if (file.exists()) {
            try {
                val encryptedData = file.readBytes()
                val decryptedData = decryptData(encryptedData, getAesKey())
                ObjectInputStream(ByteArrayInputStream(decryptedData)).use { ois ->
                    @Suppress("UNCHECKED_CAST")
                    ois.readObject() as List<Hobby>
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: List<Hobby>) {
        callback(result)
    }
    
    private fun getAesKey(): SecretKeySpec {
        val key = "MySuperSecretKey123".toByteArray()
        return SecretKeySpec(key, "AES")
    }
    
    private fun decryptData(encryptedData: ByteArray, key: SecretKeySpec): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(encryptedData)
    }
}

// Add this to your AndroidManifest.xml in the <application> tag:
// android:name=".App"
class App : android.app.Application() {
    companion object {
        lateinit var instance: App
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}