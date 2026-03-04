package com.example.greenqrsmarttreeinformationsystem.data

class FeedbackResponse(
    val error: Boolean,
    val message: String,
    val data: List<Feedback>
) {
    data class Feedback(
        var id: Int,
        var treeId: String,
        var feedback: String,
        var userName: String,
        var treeName: String,
        var scientificName: String,
        var address: String,

    )
}