package com.example.supabase

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

object SupabaseClientProvider {
    init {
        println("SUPABASE_URL: ${BuildConfig.SUPABASE_URL}")
        println("SUPABASE_KEY: ${BuildConfig.SUPABASE_KEY}")
    }

    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
    }
}

class Controller : ViewModel() {

    val supabase = SupabaseClientProvider.supabase

    var note = mutableStateListOf<Entity>()
        private set

    init {
        viewModelScope.launch {
            note.addAll(getData())
        }
    }

    suspend fun getData(): List<Entity> {
        return supabase.from("Supabase").select().decodeList<Entity>()
    }

    suspend fun insertData(noteText: String) {
        try {
            val newNote = Entity(note = noteText)
            supabase.from("Supabase").insert(newNote)

            // Refresh the list
            note.clear()
            note.addAll(getData())
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error inserting note: ${e.message}")
        }
    }

    suspend fun updateData(id: Int, noteText: String) {
        try {
            supabase.from("Supabase")
                .update({
                    set("note", noteText)
                }) {
                    filter {
                        eq("id", id)
                    }
                }

            // Refresh the list
            note.clear()
            note.addAll(getData())
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error updating note: ${e.message}")
        }
    }

    //  Delete function
    suspend fun deleteData(id: Int) {
        try {
            supabase.from("Supabase")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }

            // Refresh the list
            note.clear()
            note.addAll(getData())
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error deleting note: ${e.message}")
        }
    }
}