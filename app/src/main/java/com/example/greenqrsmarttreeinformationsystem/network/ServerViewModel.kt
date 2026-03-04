package com.example.greenqrsmarttreeinformationsystem.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenqrsmarttreeinformationsystem.data.AuthResponse
import com.example.greenqrsmarttreeinformationsystem.data.FeedbackResponse
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServerViewModel : ViewModel() {

    // ---------------- AUTH ----------------
    private val _registrationResult = MutableLiveData<AuthResponse>()
    val registrationResult: LiveData<AuthResponse> = _registrationResult

    private val _loginResult = MutableLiveData<AuthResponse>()
    val loginResult: LiveData<AuthResponse> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> = _loginError

    // ---------------- TREE LIST ----------------
    private val _treeResult = MutableLiveData<TreeResponse>()
    val treeResult: LiveData<TreeResponse> = _treeResult

    private val _treeError = MutableLiveData<String>()
    val treeError: LiveData<String> = _treeError

    // ---------------- TREE DETAILS ----------------
    private val _treeDetails = MutableLiveData<TreeResponse>()
    val treeDetails: LiveData<TreeResponse> = _treeDetails

    private val _detailError = MutableLiveData<String>()
    val detailsError: LiveData<String> = _detailError

    // ---------------- FEEDBACK ----------------
    private val _feedback = MutableLiveData<FeedbackResponse>()
    val feedback: LiveData<FeedbackResponse> = _feedback

    private val _feedError = MutableLiveData<String>()
    val feedError: LiveData<String> = _feedError

    private val _getFeedback = MutableLiveData<FeedbackResponse>()
    val getFeedback: LiveData<FeedbackResponse> = _getFeedback

    private val _getFeedbackError = MutableLiveData<String>()
    val getFeedbackError: LiveData<String> = _getFeedbackError

    // ---------------- DELETE TREE ----------------
    private val _deleteTree = MutableLiveData<TreeResponse>()
    val deleteTree: LiveData<TreeResponse> = _deleteTree

    private val _deleteError = MutableLiveData<String>()
    val deleteError: LiveData<String> = _deleteError

    // ---------------- ADD TREE ----------------
    fun addTreeDetails(
        name: String,
        scientificName: String,
        medicinalUses: String,
        description: String,
        imageUri: String,
        latitude: String,
        longitude: String,
        address: String,
        height: String,
        oxygenLevel: String,
        diameter: String,
        canopySpread: String,
        speciesType: String,
        waterNeeds: String,
        datePlanted: String,
        conservStatus: String,
        healthStatus: String,
        treeId: String,
        onResult: (TreeResponse) -> Unit
    ) {
        Log.d("TEST_DEBUG", "addTreeDetails function called")

        RetrofitInstance.retrofit.addTree(
            name, scientificName, medicinalUses, description,
            imageUri, latitude, longitude, address, height,
            oxygenLevel, diameter, canopySpread, speciesType,
            waterNeeds, datePlanted, conservStatus, healthStatus, treeId
        ).enqueue(object : Callback<TreeResponse> {

            override fun onResponse(
                call: Call<TreeResponse>,
                response: Response<TreeResponse>
            ) {

                Log.d("RAW_RESPONSE", response.raw().toString())
                Log.d("BODY_RESPONSE", response.body().toString())
                Log.d("ERROR_BODY", response.errorBody()?.string() ?: "No error body")

                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("ADD_ERROR", "Server Error: $errorMsg")

                    onResult(
                        TreeResponse(
                            true,
                            "Server rejected request",
                            emptyList()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<TreeResponse>, t: Throwable) {

                Log.e("ADD_TREE_ERROR", t.localizedMessage ?: "Unknown error")

                onResult(
                    TreeResponse(
                        true,
                        "Network error: ${t.localizedMessage}",
                        emptyList()
                    )
                )
            }
        })
    }
    // ---------------- LOGIN ----------------
    fun loginUser(email: String, password: String, condition: String) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.retrofit.loginAuth(email, password, condition)
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.postValue(response.body())
                } else {
                    _loginError.postValue("Login failed")
                }
            } catch (e: Exception) {
                _loginError.postValue(e.localizedMessage)
            }
        }
    }

    // ---------------- REGISTER ----------------
    fun registerUser(username: String, phone: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.retrofit.addAuth(username, phone, email, password)
                if (response.isSuccessful && response.body() != null) {
                    _registrationResult.postValue(response.body())
                } else {
                    _error.postValue("Registration failed")
                }
            } catch (e: Exception) {
                _error.postValue(e.localizedMessage)
            }
        }
    }

    // ---------------- FEEDBACK ----------------
    fun feedbackReview(
        treeId: String,
        feedback: String,
        userName: String,
        treeName: String,
        scientificName: String,
        address: String
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.retrofit.addFeedback(
                    treeId, feedback, userName, treeName, scientificName, address
                )
                if (response.isSuccessful && response.body() != null) {
                    _feedback.postValue(response.body())
                } else {
                    _feedError.postValue("Feedback failed")
                }
            } catch (e: Exception) {
                _feedError.postValue(e.localizedMessage)
            }
        }
    }

    // ---------------- TREE LIST ----------------
    fun getTreeDetails(condition: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.retrofit.getTreeDetails(condition)
                if (response.isSuccessful && response.body() != null) {
                    _treeResult.postValue(response.body())
                } else {
                    _treeError.postValue("Tree fetch failed")
                }
            } catch (e: Exception) {
                _treeError.postValue(e.localizedMessage)
            }
        }
    }

    // ---------------- TREE DETAILS ----------------
    fun getSpecificTree(condition: String, id: Int) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.retrofit.getSpecificTree(condition, id)
                if (response.isSuccessful && response.body() != null) {
                    _treeDetails.postValue(response.body())
                } else {
                    _detailError.postValue("Tree details failed")
                }
            } catch (e: Exception) {
                _detailError.postValue(e.localizedMessage)
            }
        }
    }

    // ---------------- FEEDBACK LIST ----------------
    fun getFeedbackReview(condition: String) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.retrofit.getFeedBack(condition)
                if (response.isSuccessful && response.body() != null) {
                    _getFeedback.postValue(response.body())
                } else {
                    _getFeedbackError.postValue("Feedback fetch failed")
                }
            } catch (e: Exception) {
                _getFeedbackError.postValue(e.localizedMessage)
            }
        }
    }

    // ---------------- DELETE TREE ----------------
    fun deleteTree(condition: String, id: Int) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.retrofit.deleteTree(condition, id)
                if (response.isSuccessful && response.body() != null) {
                    _deleteTree.postValue(response.body())
                } else {
                    _deleteError.postValue("Delete failed")
                }
            } catch (e: Exception) {
                _deleteError.postValue(e.localizedMessage)
            }
        }
    }
}