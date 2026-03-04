package com.example.greenqrsmarttreeinformationsystem.network

import com.example.greenqrsmarttreeinformationsystem.data.AuthResponse
import com.example.greenqrsmarttreeinformationsystem.data.FeedbackResponse
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface apiService {
    @FormUrlEncoded
    @POST("addTree.php")
    fun addTree(
        @Field("name") name: String,
        @Field("scientificName") scientificName: String,
        @Field("details") medicinalUses: String,  // సర్వర్ 'details' అడుగుతుంది, మనం మన 'medicinalUses' పంపిస్తాం
        @Field("description") description: String,
        @Field("imageUri") imageUri: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("address") address: String,
        @Field("height") height: String,
        @Field("age") oxygenLevel: String,         // సర్వర్ 'age' అడుగుతుంది, మనం మన 'oxygenLevel' పంపిస్తాం
        @Field("diameter") diameter: String,
        @Field("canopySpread") canopySpread: String,
        @Field("speciesType") speciesType: String,
        @Field("waterNeeds") waterNeeds: String,
        @Field("datePlanted") datePlanted: String,
        @Field("conservStatus") conservStatus: String,
        @Field("healthStatus") healthStatus: String,
        @Field("treeId") treeId: String
    ): Call<TreeResponse>

    @FormUrlEncoded
    @POST("signup.php")
    suspend fun addAuth(
        @Field("username") userName: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>


    @FormUrlEncoded
    @POST("addFeedback.php")
    suspend fun addFeedback(
        @Field("treeId") treeId: String,
        @Field("feedback") feedback: String,
        @Field("userName") userName: String,
        @Field("treeName") treeName: String,
        @Field("scientificName") scientificName: String,
        @Field("address") address: String,
    ): Response<FeedbackResponse>

    @FormUrlEncoded
    @POST("allFunction.php")
    suspend fun loginAuth(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("condition") condition: String
    ): Response<AuthResponse>


    @FormUrlEncoded
    @POST("allFunction.php")
    suspend fun getTreeDetails(
        @Field("condition") condition: String
    ): Response<TreeResponse>

    @FormUrlEncoded
    @POST("allFunction.php")
    suspend fun getFeedBack(
        @Field("condition") condition: String
    ): Response<FeedbackResponse>

    @FormUrlEncoded
    @POST("allFunction.php")
    suspend fun getSpecificTree(
        @Field("condition") condition: String,
        @Field("id") id: Int
    ): Response<TreeResponse>

    @FormUrlEncoded
    @POST("allFunction.php")
    suspend fun deleteTree(
        @Field("condition") condition: String,
        @Field("id") id: Int
    ): Response<TreeResponse>


}