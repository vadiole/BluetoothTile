package vadiole.bluetoothtile

import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.core.view.WindowInsetsCompat.Type.navigationBars
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import java.util.concurrent.Executor
import java.util.function.Consumer
import vadiole.bluetoothtile.ui.Density
import vadiole.bluetoothtile.ui.matchParent

class HomeActivity : Activity(), Density {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDecorFitsSystemWindows(window, false)
        setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(
                0, insets.getInsets(statusBars()).top,
                0, insets.getInsets(navigationBars()).bottom
            )
            insets
        }

        setContentView(
            TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 14f.sp)
                setTextColor(Color.GRAY)
                text = "BluetoothTile by vadiole"
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val statusBarManager = getSystemService(StatusBarManager::class.java)
            statusBarManager.requestAddTileService(
                ComponentName(this, BluetoothTileService::class.java),
                getString(R.string.bluetooth_tile_label),
                Icon.createWithResource(this, R.drawable.ic_bluetooth_24px),
                Executor { },
                Consumer { }
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateSystemBars(newConfig)
        window.decorView.setBackgroundColor(getColor(R.color.window_background))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            updateSystemBars(resources.configuration)
        }
    }

    private fun updateSystemBars(configuration: Configuration) {
        val isNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = !isNightMode
        insetsController.isAppearanceLightNavigationBars = !isNightMode
    }
}