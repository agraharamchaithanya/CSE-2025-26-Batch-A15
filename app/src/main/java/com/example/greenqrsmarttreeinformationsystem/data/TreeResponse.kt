package com.example.greenqrsmarttreeinformationsystem.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

data class TreeResponse(
    val error: Boolean,
    val message: String,
    val data: List<Tree>
) {

    @Parcelize
    data class Tree(
        val id: Int,
        val name: String,
        val scientificName: String,
        @SerializedName("details") // సర్వర్ లో ఉన్న details ని medicinalUses కి ఇస్తున్నాం
        val medicinalUses: String,
        val description: String,
        val imageUri: String,
        val latitude: String,
        val longitude: String,
        val address: String,
        val height: String,
        @SerializedName("age")
        val oxygenLevel: String,
        val diameter: String,
        val canopySpread: String,
        val speciesType: String,
        val waterNeeds: String,
        val datePlanted: String,
        val conservStatus: String,
        val healthStatus: String,
        val treeId: String
    ): Parcelable{


    }
}