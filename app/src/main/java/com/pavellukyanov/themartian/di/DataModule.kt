package com.pavellukyanov.themartian.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.MartianLocalDatabase
import com.pavellukyanov.themartian.utils.C.DB_NAME
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE photo ADD COLUMN srcSmall TEXT")
        db.execSQL("ALTER TABLE photo ADD COLUMN srcMedium TEXT")
        db.execSQL("ALTER TABLE photo ADD COLUMN srcLarge TEXT")
        db.execSQL("ALTER TABLE photo ADD COLUMN srcFull TEXT")
        db.execSQL("ALTER TABLE photo ADD COLUMN isPanoramaPart INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE photo ADD COLUMN panoramaSequenceId TEXT")
        db.execSQL("ALTER TABLE photo ADD COLUMN hasStereoPair INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE photo ADD COLUMN stereoPairId INTEGER")
        db.execSQL("ALTER TABLE photo ADD COLUMN lightingConditions TEXT")
        db.execSQL("ALTER TABLE photo ADD COLUMN locationVisits INTEGER")
    }
}

val dataModule = module {
    //Api
    factory { ApiDataSource(roverService = get(), networkMonitor = get()) }

    //Cache
    single {
        Room.databaseBuilder(
            androidApplication(),
            MartianLocalDatabase::class.java,
            DB_NAME
        )
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    //Dao
    single { get<MartianLocalDatabase>().roverInfo() }
    single { get<MartianLocalDatabase>().favourites() }
    single { get<MartianLocalDatabase>().cameras() }
}
