package com.example.noteapp.di

import android.app.Application
import androidx.room.Room
import com.example.noteapp.note.data.data_source.local.NoteDatabase
import com.example.noteapp.note.data.repository.NoteRepositoryImpl
import com.example.noteapp.note.domain.repository.NoteRepository
import com.example.noteapp.note.domain.usecases.DeleteNoteUsesCase
import com.example.noteapp.note.domain.usecases.FetchNoteByIdUseCase
import com.example.noteapp.note.domain.usecases.GetAllNotesUseCase
import com.example.noteapp.note.domain.usecases.InsertNoteUseCase
import com.example.noteapp.note.domain.usecases.SearchNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    fun provideUseCases(repository: NoteRepository): UseCases {
        return UseCases(
            GetAllNotesUseCase(repository),
            DeleteNoteUsesCase(repository),
            InsertNoteUseCase(repository),
            SearchNoteUseCase(repository),
            FetchNoteByIdUseCase(repository)
        )
    }
}