package uk.ac.tees.w9617343.newshub.screens.saved

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uk.ac.tees.w9617343.newshub.models.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    val savedNewsList: MutableState<List<Article>> = mutableStateOf(emptyList())

    fun getNewsInRealTime() {
        val currentUserId = auth.currentUser?.uid
        firestore.collection("users").document(currentUserId!!).collection("articles")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Log.d("SavedViewModel", "realtimeUpdates: ${it.message}")
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    val list = mutableListOf<Article>()
                    for (articleDocument in querySnapshot) {
                        list.add(articleDocument.toObject(Article::class.java))
                    }
                    savedNewsList.value = list
                }
            }
    }


}