package eu.tutorials.tourguideapp.models

// TODO Step : Create a data model class for users that we will use to set the data and store it to the cloud firestore also will use it for the RecyclerView.
// START
// A data model class for users that we will use to set the values and store it to the cloud firestore also will use it for the RecyclerView.
data class User(
    var id: String = "",
    var name: String = "",
    var email: String = ""
)
// END