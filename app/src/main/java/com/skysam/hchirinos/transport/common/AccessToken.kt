package com.skysam.hchirinos.transport.common

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.collect.Lists
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

object AccessToken {
    private const val ENDPOINT_FCM = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken(): String {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"transport-5161f\",\n" +
                    "  \"private_key_id\": \"f6b12e4288b3a32a596ca0665f0ec03a3534db60\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDGT7XjjpbrtToZ\\n5ntDVx3GUK/cL6KL2i5lv2EvXYOe6u/RmOhdfG++2/eRvCltk2s+UtXzuCzmwhpU\\ngk8GZt7jhIfvK+0sApU1yqw42Y4c4tqB0jb3ZILMTT6pR5XQDuOJHpp4aL6Qa5Ml\\nDQRTEF9+zoqh9CYZhuVBKi89RnL5OkdDeL2ZkUIFtxphgZf5ZEuOLs/Rm7j5nfiF\\n8bFaYkrnANsaVeuVp7DJPvNGOzPSd4um8GiM86uzWZk94BzW0g1Gi2QxXIEJ3+DT\\naGmh40aFemsyz2TMWZ5mcXVz8HB60kpV60F9sPWlkFow8kqaJ7l9jo6j/9XoGgqk\\ndsyFNs9FAgMBAAECggEAC6OpCsqXLA1udA+1g13NFxMsRzGaijfLilhBtXx5Ga4z\\nVitx0+7WRi716F1RVyeUX0uQWCYiqIaS8gvV5IvMIDFqowyCdpdON6KGGi4Wq7Za\\n2czDQiDplmEgQOI9NkFxdF5R9ag8KxDNSZrqeo+W80PfGiNQyyXCNeFqdV+5psv8\\n4Z/ZtvfUqw1YRngalvZ6rTbA5I2JFtMbJKIRaHKdd0bKXC9ki/dXXLSypFeFfap4\\nKnDn6ePWHZKW0wEU1x5B/YbLRXgxd9MgaSigrPzKnVhA9YlZqQdnf9ZEgT7q34Jj\\n3dGGiMsPtuq2rYwuNettjYopx8NPY5FSDK4XS3Vt4QKBgQDr837IjOuepk++RmM3\\nIDhsxXHYX19hSrKOGr496BiS8SyaoiQHxNGkH+KtpxZT2M2oJvncBWXEUexyXSvF\\njIugN99Kq0HKZ9wz+fumodPpc4mAWUK01I98brpnWLg0LAcz7RXzikUkiEC1Frmn\\nqf/I+RyF9gF3JjRHRu36PtADlQKBgQDXKXWgEm8nlwrC6TYRT9UkHt0a5i11TyKo\\nXVf8g2yQu+B0lTqX0gqjeEXad5EaVKVGPRrUnv96Vn92CZuxbJxGASSTbumiTKcM\\ns42VeDm36ED2LY2sLrE9Vqt8OBN04jFjqjJKTW3YovTTX5R2vVUytR2FtM803qsO\\nAXKYEfmw8QKBgFvSRmSFcqwqzJ2fhrzNLSgnLkal1thl2Vaz0ZUF3YpRkKocryjW\\nzz6vqecqPuYiWOY3RMT61L+xofJfj8kxCKUxGRRV0SSThn4zqlsOXwlajQgFtI/q\\nqGwkKyZMwGKNP8BVY1WDWaT94TCXx2gYIBJ130PCmI9lrrKOR84643iBAoGAOQRU\\nllmsOQqOoH4ahNXp/otrPlCW5UpWPdWWlmThZS1UWrCc+VSA2zbpE+6xdg/OSGGy\\n9Xs46lt3pXkAWDEW7HorDiLLKOY1Mml2cJCOgwy6AkKqE2HAOCf6g418onG6ctXM\\nQ+Jm5IyyYj0m45xisnBKZX83laQYIjsB+ghxaZECgYBMICGUDURe2rKquWWVSvVq\\njL110wTHHfawcILuXG6nT8TybbmdLRUYoEdfiQ5sNjb3xJeQq8CwzYAnUR1OAbfx\\nORb28Uek/XMu/nCaQByBVtEltd41zUSvPaA7xBGAupCdcqoLaQ0WAgKmeQxVj6Gt\\nMYptxWGfGmBJcxD0ND4ICA==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-q43mr@transport-5161f.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"114449933474552977602\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-q43mr%40transport-5161f.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"

            val stream: InputStream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))
            val googleCredential = GoogleCredentials.fromStream(stream).createScoped(Lists.newArrayList(
                ENDPOINT_FCM))
            googleCredential.refresh()
            return googleCredential.accessToken.tokenValue
        } catch (e: Exception) {
            Log.e("Error", e.message?:"")
            return "Error"
        }
    }
}