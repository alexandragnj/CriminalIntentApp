package com.example.criminalintentapp

import android.R
import android.app.Application
import com.example.criminalintentapp.utils.appModule
import com.example.criminalintentapp.utils.appModuleDB
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/king_richard.ttf")
                            .build()
                    )
                )
                .build()
        )

        startKoin {
            androidLogger()
            androidContext(this@CriminalIntentApplication)
            modules(appModule, appModuleDB)
        }
    }
}
