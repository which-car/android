package com.mahmutalperenunal.whichcar

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager

class Permissions : Application() {

    override fun onCreate() {
        super.onCreate()

        registerListener()
    }

    private fun registerListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstance: Bundle?) {
                //not allowed take a screenshot
                activity.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
            }

            override fun onActivityStarted(activity: Activity) { }

            override fun onActivityResumed(activity: Activity) { }

            override fun onActivityPaused(activity: Activity) { }

            override fun onActivityStopped(activity: Activity) { }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

            override fun onActivityDestroyed(activity: Activity) { }

        })
    }

}