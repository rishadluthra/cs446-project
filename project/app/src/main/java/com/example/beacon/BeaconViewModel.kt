package com.example.beacon

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

data class UiState(
    var name: String = "testName", //TODO: Set this to the dummy name from the backend!!!!
    var ourBeacons: List<MyBeaconsInfo> = emptyList(),
    var nearbyBeacons: List<BeaconInfo> = emptyList()

    var ourReviews: List<Review> = emptyList(),
    var ourEmail: String = "",
    var ourAverageRating: Int = -1,

    var searchedReviews: List<Review> = emptyList(),
    var validSearch: Boolean = true,
    var searchOverallRating: Int = -1
)
@Serializable
data class BeaconInfo(val id: String, val creatorId: String, val title: String, val description: String, val location: Location, val tag: String, val createdAt: String, val updatedAt: String, val creatorEmail: String)

@Serializable
data class MyBeaconsInfo(val id: String, val creatorId: String, val title: String, val description: String, val location: Location, val tag: String, val createdAt: String, val updatedAt: String)
@Serializable
data class Location(val type: String, val coordinates: Array<Float>)
@Serializable
data class Review(val creatorId: String, val targetId: String, val rating: Int, val review: String, val createdAt: String, val updatedAt: String, val id: String)


class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    var themeStrategy: MutableState<ThemeStrategy> = mutableStateOf(LightThemeStrategy)

    fun refreshOurBeacons() {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    ourBeacons = fetchOurBeacons().asList(),
                )
            }
        }
    }

    fun refreshOurEmail() {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    ourEmail = getMyEmail()
                )
            }
        }
    }

    fun refreshNearby(tags: List<String>?, maxDistanceKm: Int) {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    nearbyBeacons = fetchNearbyBeacons(tags, maxDistanceKm).asList()
                )
            }
        }
    }

    fun refreshOurReviews() {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    ourReviews = getMyReviews().asList(),
                    ourAverageRating = getMyAverageRating(),
                    validSearch = true
                )
            }
        }
    }

     fun refreshSearchedReviews(email: String) {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    searchedReviews = getReviewsByTargetEmail(email).asList(),
                    searchOverallRating = getMyAverageRatingByTargetEmail(email),
                    validSearch = checkUserValidity(email)
                )
            }
        }
    }
    fun sendBeacon(title: String,
                   tag: String,
                   description: String,
                   postalCode: String,
                   onSuccess: (Int) -> Unit,
                   onError: (Int) -> Unit) {
        viewModelScope.launch {
            // Perform the network operation on a background thread
            val responseCode = withContext(Dispatchers.IO) {
                val newBeaconJsonObject = buildJsonObject {
                    put("title", title)
                    put("tag", tag)
                    put("description", description)
                    put("postalCode", postalCode)
                }
                val newBeaconJsonString =
                    Json.encodeToString(JsonObject.serializer(), newBeaconJsonObject)
                postBeacon(newBeaconJsonString)
            }
            // Now back on the main thread, check the response and call onSuccess or onError
            if (responseCode == 201) {
                onSuccess(responseCode)
            } else {
                onError(responseCode)
            }
        }
    }
    
    fun updateBeacon(title: String,
                     tag: String,
                     description: String,
                     postalCode: String,
                     beaconId: String,
                     onSuccess: (Int) -> Unit,
                     onError: (Int) -> Unit
    ) {
        viewModelScope.launch {
            // Perform the network operation on a background thread
            val responseCode = withContext(Dispatchers.IO) {
                val newBeaconJsonObject = buildJsonObject {
                    put("title", title)
                    put("tag", tag)
                    put("description", description)
                    put("postalCode", postalCode)
                }
                val newBeaconJsonString =
                    Json.encodeToString(JsonObject.serializer(), newBeaconJsonObject)
                patchBeacon(newBeaconJsonString, beaconId)
            }
            // Now back on the main thread, check the response and call onSuccess or onError
            if (responseCode == 200) {
                onSuccess(responseCode)
            } else {
                onError(responseCode)
            }
        }
    }


    fun delete(id: String,
               onSuccess: (Int) -> Unit,
               onError: (Int) -> Unit) {
        viewModelScope.launch {
            val responseCode = withContext(Dispatchers.IO) {
                deleteBeacon(id)
            }
            if (responseCode == 200) {
                onSuccess(responseCode)
            } else {
                onError(responseCode)
            }
        }
    }

    fun checkUserValidity(reportEmail: String): Boolean {
        var responseCode = false
        val thread = thread(start = true) {
            responseCode = isUserValid(reportEmail)
        }
        thread.join()
        Log.d("checkUserValidity", "------------------")
        Log.d("checkUserValidity", "$reportEmail $responseCode")
        return responseCode

    }

    fun toggleTheme() {
        themeStrategy.value = if (themeStrategy.value == DarkThemeStrategy) {
            LightThemeStrategy
        } else {
            DarkThemeStrategy
        }
        print("current theme: ${themeStrategy.value}")
    }
    fun signIn(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (Int) -> Unit
    ) {
        // Launch a coroutine in the ViewModelScope
        viewModelScope.launch {
            // Perform the network operation on a background thread
            val (responseCode, authToken) = withContext(Dispatchers.IO) {
                val signInJsonObject = buildJsonObject {
                    put("email", email)
                    put("password", password)
                }
                val signInJsonString =
                    Json.encodeToString(JsonObject.serializer(), signInJsonObject)
                postSignIn(signInJsonString)
            }
            // Now back on the main thread, check the response and call onSuccess or onError
            if (responseCode == 201 && authToken != "") {
                AuthManager.setAuthToken(authToken)
                onSuccess(authToken)
            } else {
                onError(responseCode)
            }
        }
    }
    fun reportUser(reportEmail: String): Int {
        var responseCode = 0
        thread {
            val reportEmailJsonObject = buildJsonObject {
                put("reportEmail", reportEmail)
            }
            val reportEmailJsonString =
                Json.encodeToString(JsonObject.serializer(), reportEmailJsonObject)
            responseCode = postReportUser(reportEmailJsonString)
        }
        return responseCode
    }

    fun reviewUser(targetEmail: String, rating: Int, review: String): Int {
        var responseCode = 0
        thread {
            val reviewJsonObject = buildJsonObject {
                put("targetEmail", targetEmail)
                put("rating", rating)
                put("review", review)
            }
            val reviewJsonString =
                Json.encodeToString(JsonObject.serializer(), reviewJsonObject)
            responseCode = postReview(reviewJsonString)
        }
        return responseCode
    }
    fun createAccountAndSignIn(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (Int) -> Unit
    ) {
        // Launch a coroutine in the ViewModelScope
        viewModelScope.launch {
            // Perform the network operation on a background thread
            val (responseCode, authToken) = withContext(Dispatchers.IO) {
                val newUserJsonObject = buildJsonObject {
                    put("firstName", firstName)
                    put("lastName", lastName)
                    put("email", email)
                    put("password", password)
                }
                val createAccountJsonString =
                    Json.encodeToString(JsonObject.serializer(), newUserJsonObject)
                postRegisterAndSignIn(createAccountJsonString)
            }
            // Now back on the main thread, check the response and call onSuccess or onError
            if (responseCode == 201 && authToken != "") {
                AuthManager.setAuthToken(authToken)
                onSuccess(authToken)
            } else {
                onError(responseCode)
            }
        }
    }

    fun sendEmailAndVerify(
        email: String,
        onSuccess: (String) -> Unit,
        onError: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val (responseCode, verification) = withContext(Dispatchers.IO) {
                val newEmailJsonObject = buildJsonObject {
                    put("email", email)
                }
                val newEmailJsonString =
                    Json.encodeToString(JsonObject.serializer(), newEmailJsonObject)
                getVerificationCode(newEmailJsonString)
            }
            if (responseCode == 201 && verification != "") {
                VerificationManager.setVerificationCode(verification)
                onSuccess(verification)
            } else {
                onError(responseCode)
            }
        }
    }
}


fun fetchOurBeacons(): Array<MyBeaconsInfo> {
    try {
        val authToken = AuthManager.getAuthToken()
        val request = okhttp3.Request.Builder()
            .url("http://10.0.2.2:4000/beacons/my_beacons")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = OkHttpClient().newCall(request).execute()
        val json = response.body!!.string()
        return Json.decodeFromString<Array<MyBeaconsInfo>>(json)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun fetchNearbyBeacons(tags: List<String>?, maxDistanceKm: Int): Array<BeaconInfo> {
    val maxDistance = maxDistanceKm * 1000
    val baseUrl = "http://10.0.2.2:4000/beacons?latitude=43.475807&longitude=-80.542007"
    val distUrl = "$baseUrl&maxDistance=$maxDistance"

    val url: String = if (!tags.isNullOrEmpty()) {
        val tagsQueryString = tags.joinToString("&") { "tags[]=$it" }
        "$distUrl&$tagsQueryString"
    } else {
        val allTags = "&tags[]=labour&tags[]=tools&tags[]=tech&tags[]=social"
        "$distUrl$allTags"
    }
    try {
        val authToken = AuthManager.getAuthToken()
        val request = okhttp3.Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = OkHttpClient().newCall(request).execute()
        val json = response.body!!.string()
        return Json.decodeFromString<Array<BeaconInfo>>(json)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun deleteBeacon(id: String): Int {
    try {
        val authToken = AuthManager.getAuthToken()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons/$id")
            .addHeader("Authorization", "Bearer $authToken")
            .delete()
            .build()
        val response = OkHttpClient().newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 400
}

fun postBeacon(newBeaconJsonString: String): Int {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = newBeaconJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val authToken = AuthManager.getAuthToken()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons")
            .addHeader("Authorization", "Bearer $authToken")
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 400
}

fun patchBeacon(newBeaconJsonString: String, beaconId: String): Int {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = newBeaconJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val authToken = AuthManager.getAuthToken()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons/${beaconId}")
            .addHeader("Authorization", "Bearer $authToken")
            .patch(requestBody)
            .build()
        val response = client.newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 400
}

suspend fun postSignIn(signInJsonString: String): Pair<Int, String> {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = signInJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/auth/login")
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody.toString())
                val authToken = jsonObject.optString("access_token", "")
                return Pair(response.code, authToken)
            }
        }
    } catch (e: Exception) {
            println(e.message)
    }
        return Pair(400, "") // Indicate a client error in case of exception
}

suspend fun postRegisterAndSignIn(registerJsonString: String): Pair<Int, String> {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = registerJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/auth/register")
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody.toString())
                val authToken = jsonObject.optString("access_token", "")
                return Pair(response.code, authToken)
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
    return Pair(400, "") // Indicate a client error in case of exception
}

fun getVerificationCode(verifyJsonString: String): Pair<Int, String> {
    var clientErrorCode = -1
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = verifyJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/auth/send-verification-email")
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val verificationCode = response.body?.string().toString()
                return Pair(response.code, verificationCode)
            } else {
                clientErrorCode = response.code
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
    return Pair(clientErrorCode, "") // Indicate a client error in case of exception
}

fun getMyReviews(): Array<Review> {
    try {
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/reviews/my_reviews")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        val jsonObject = JSONObject(jsonString)
        val jsonReviews = jsonObject.getString("reviews")
        return Json.decodeFromString<Array<Review>>(jsonReviews)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun getReviewsByTargetEmail(targetEmail: String): Array<Review> {
    try {
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/reviews?targetEmail=$targetEmail")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        val jsonObject = JSONObject(jsonString)
        val jsonReviews = jsonObject.getString("reviews")
        return Json.decodeFromString<Array<Review>>(jsonReviews)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun getMyAverageRating(): Int {
    try {
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/reviews/my_reviews")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        val jsonObject = JSONObject(jsonString)
        return jsonObject.getString("averageRating").toFloat().toInt()
    } catch (e: Exception) {
        println(e.message)
    }
    return -1
}

fun getMyAverageRatingByTargetEmail(targetEmail: String): Int {
    try {
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/reviews?targetEmail=$targetEmail")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        val jsonObject = JSONObject(jsonString)
        Log.d("isUserValid", "------------------")
        Log.d("isUserValid", "$targetEmail ${jsonObject.getString("averageRating")}")
        return jsonObject.getString("averageRating").toFloat().toInt()
    } catch (e: Exception) {
        println(e.message)
    }
    return -1
}

fun postReview(reviewJsonString: String): Int {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = reviewJsonString.toRequestBody(mediaType)
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/reviews")
            .addHeader("Authorization", "Bearer $authToken")
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 0
}

fun postReportUser(targetEmailJsonString: String): Int {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = targetEmailJsonString.toRequestBody(mediaType)
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/reports")
            .addHeader("Authorization", "Bearer $authToken")
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 0
}

fun getMyEmail(): String {
    try {
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/users/my_email")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = client.newCall(request).execute()
        return response.body!!.string()
    } catch (e: Exception) {
        println(e.message)
    }
    return "Not Found"
}

fun isUserValid(targetEmail: String): Boolean {
    try {
        val authToken = AuthManager.getAuthToken()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/users?targetEmail=$targetEmail")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = client.newCall(request).execute()
        val string = response.body!!.string()
        Log.d("isUserValid", "------------------")
        Log.d("isUserValid", "$targetEmail $string")
        return string.toBoolean()
    } catch (e: Exception) {
        println(e.message)
    }
    return false
}



