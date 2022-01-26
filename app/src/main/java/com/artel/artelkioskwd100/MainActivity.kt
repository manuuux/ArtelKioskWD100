package com.artel.artelkioskwd100
//Descomentar para construir APK
import android.bluetooth.BluetoothAdapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.bluetooth.BluetoothProfile.GATT
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.content.Intent
import android.view.KeyEvent
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import android.view.inputmethod.EditorInfo

import android.widget.TextView

import android.widget.TextView.OnEditorActionListener
import android.bluetooth.BluetoothManager

import android.R.string.no
import android.bluetooth.BluetoothProfile

import android.bluetooth.BluetoothDevice

import android.R.string.no
import android.app.StatusBarManager














class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //no deja abrir el top de notificaciones
        //getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        setContentView(R.layout.activity_main)
        val SSID: TextView = findViewById(R.id.TXT_Wifi) as TextView
        val BT: TextView = findViewById(R.id.TXT_Bt) as TextView
        val btnwifi = findViewById<ImageButton>(R.id.BTN_wifi)
        val btnconfig = findViewById<ImageButton>(R.id.BTN_Config)
        val btnbt = findViewById<ImageButton>(R.id.BTN_bt)


        //Descomentar al crear APK
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //val devices: List<BluetoothDevice> = bluetoothManager.getConnectedDevices(GATT)
        btnbt.setOnClickListener {
            val pairedDevices = mBluetoothAdapter.bondedDevices
            //val connectedDevices = mBluetoothAdapter.getConnectedDevices(GATT)
            // Enable or disable the Bluetooth and display
            // the state in Text View
            if (mBluetoothAdapter.isEnabled) {
                btnbt.setImageResource(R.drawable._52059_bluetooth_disabled_icon)
                mBluetoothAdapter.disable()
                BT.setVisibility(View.GONE)
            } else {
                mBluetoothAdapter.enable()
                btnbt.setImageResource(R.drawable._52057_bluetooth_icon)
                BT.setVisibility(View.VISIBLE)
                for (bt in pairedDevices) BT.text = bt.name
            }
        }

        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                    val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                    val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
                    val ssid = wifiManager.connectionInfo
                    SSID.text = ssid.ssid.toString()
                    btnwifi.setOnClickListener {wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled}
                    if (wifiManager.isWifiEnabled) {
                        btnwifi.setImageResource(R.drawable._52129_bar_signal_wifi_icon);
                        SSID.setVisibility(View.VISIBLE);
                    } else {
                        btnwifi.setImageResource(R.drawable._52130_off_signal_wifi_icon);
                        SSID.setVisibility(View.GONE);
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(broadCastReceiver, intentFilter)


        //Descomentar para construir APK
        //val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //val pairedDevices = mBluetoothAdapter.bondedDevices
        //for (bt in pairedDevices) BT.text = bt.name
        val btnlauncher = findViewById<ImageButton>(R.id.BTN_Launcher)
        btnlauncher.setOnClickListener {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.linetec.artelletra2ddb")
            launchIntent?.let { startActivity(it) }
        }

/*
        fun showDialog(activity: Activity?, msg: String?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialoglogin)

            dialog.show()
        }

        btnconfig.setOnClickListener(){
            showDialog(getActivity(), "Error de conexión al servidor")
        }
*/

        class ViewDialog {
            fun showResetPasswordDialog(activity: Activity?) {
                val dialog = Dialog(activity!!)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialoglogin)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val btnentrar = dialog.findViewById<Button>(R.id.BTN_Ingresar)
                val txtcontrasena = dialog.findViewById<EditText>(R.id.PSW_Contra)
                txtcontrasena.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                    if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                        //do what you want on the press of 'done'
                        btnentrar.performClick()
                    }
                    false
                })
                btnentrar.setOnClickListener(View.OnClickListener {
                    val textContra = txtcontrasena.getText().toString()
                    if (textContra == "artel99123"){
                        dialog.dismiss()
                        startActivity(

                            Intent(
                                this@MainActivity,
                                administracion::class.java
                            )
                        )
                    }else{
                        Toast.makeText(this@MainActivity, "Contraseña Erronea", Toast.LENGTH_SHORT).show()
                    }

                })
                dialog.window!!.setBackgroundDrawableResource(R.drawable.drawableroundeddialog);
                val dialogBtn_remove = dialog.findViewById<TextView>(R.id.txtClose)
                dialogBtn_remove.setOnClickListener {
                    dialog.dismiss()
                    showHide()
                    //activity!!.finish()
                }
                dialog.show()
            }
        }
        btnconfig.setOnClickListener(){
            val alert = ViewDialog()
            alert.showResetPasswordDialog(this)
            showHide()


        }
    }
    var shown = true
    private fun showHide() {
        val w = this.window
        if (shown) {
            w.setFlags(
                0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        shown = !shown
    }

}


