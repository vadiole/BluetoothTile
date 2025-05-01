package vadiole.bluetoothtile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class BluetoothTileService : TileService() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onStartListening() {
        val tile = qsTile
        tile.state = if (bluetoothAdapter.isEnabled) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }

    override fun onStopListening() {
        val tile = qsTile
        tile.state = if (bluetoothAdapter.isEnabled) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
            return
        }

        val tile = qsTile
        val currentState = tile.state
        val newState = if (currentState == Tile.STATE_ACTIVE) {
            Tile.STATE_INACTIVE
        } else {
            Tile.STATE_ACTIVE
        }
        tile.state = newState
        tile.updateTile()
        if (newState == Tile.STATE_ACTIVE) {
            bluetoothAdapter.enable()
        } else {
            bluetoothAdapter.disable()
        }
    }
}