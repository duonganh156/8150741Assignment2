package com.roam.assignment2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Entry point that triggers Hilt's code generation and hosts the application-level container. */
@HiltAndroidApp
class RoamApplication : Application()