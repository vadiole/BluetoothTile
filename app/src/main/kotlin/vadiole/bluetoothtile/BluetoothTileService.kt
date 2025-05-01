package vadiole.bluetoothtile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class BluetoothTileService : TileService() {

    @Suppress("DEPRECATION")
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var enabledIcon: Icon? = null
    private var disabledIcon: Icon? = null

    override fun onCreate() {
        super.onCreate()
        enabledIcon = Icon.createWithResource(this, R.drawable.ic_bluetooth_24px)
        disabledIcon = Icon.createWithResource(this, R.drawable.ic_bluetooth_disabled_24px)
    }

    override fun onStartListening() {
        updateTileState(qsTile, bluetoothAdapter.isEnabled)
    }

    override fun onStopListening() {
        updateTileState(qsTile, bluetoothAdapter.isEnabled)
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        if (!checkPermission()) {
            return
        }
        val tile = qsTile
        val enableBluetooth = tile.state == Tile.STATE_INACTIVE
        updateTileState(tile, enableBluetooth)
        if (enableBluetooth) {
            @Suppress("DEPRECATION")
            bluetoothAdapter.enable()
        } else {
            @Suppress("DEPRECATION")
            bluetoothAdapter.disable()
        }
    }

    private fun updateTileState(tile: Tile, enabled: Boolean) {
        tile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.icon = if (enabled) enabledIcon else disabledIcon
        tile.updateTile()
    }

    private fun checkPermission(): Boolean {
        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        val intent = Intent(this, HomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent)
        } else {
            @SuppressLint("StartActivityAndCollapseDeprecated")
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        enabledIcon = null
        disabledIcon = null
    }
}