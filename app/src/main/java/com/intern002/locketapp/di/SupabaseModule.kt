package com.intern002.locketapp.di

import com.intern002.locketapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import javax.inject.Singleton
import com.intern002.locketapp.BuildConfig.SUPABASE_KEY
import com.intern002.locketapp.BuildConfig.SUPABASE_URL
import io.ktor.client.engine.okhttp.OkHttp
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            httpEngine = OkHttp.create()

            install(Postgrest)
            install(Realtime) {
                reconnectDelay = 10.seconds
            }
        }
    }
}
