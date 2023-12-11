package com.example.nodam
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mobilegpsexam.RequestPermissionsUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.io.OutputStream
import java.util.Locale
import java.util.UUID


class MainHomeFragment : Fragment() {
    private var bluetoothAdapter: BluetoothAdapter? = null // 객체저장
    private var bluetoothSocket: BluetoothSocket? = null // 통신소켓
    private var outputStream: OutputStream? = null // 데이터를 보내기 위한 방법

    private val REQUEST_ENABLE_BT = 1
    private val PERMISSION_REQUEST_BLUETOOTH = 2

    private var isConnected: Boolean = false
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_main_home, container, false)
        //Log.d("??","test FOR adapter")
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        bluetoothAdapter = bluetoothManager?.adapter
        //Log.d("adapter","${bluetoothAdapter}")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_REQUEST_BLUETOOTH
            )
        } else {
            // Bluetooth 권한이 이미 허용된 경우 초기화 진행
            initializeBluetooth()
        }
        RequestPermissionsUtil(requireContext()).requestLocation()
        gpsRenewEvent()
        setRemainCount()
        gpsRenewBtnEvent()
        smokeEvent()

        //GlobalScope.launch(Dispatchers.Default) {

        //}
        return view
    }

    fun setRemainCount(){
        val date = DateParse()

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        var savedValue = sharedPreferences.getInt("smokeMaxCount"+date.getCurrentDate(), -1) //오늘의 최대값을 가져오기
        var information = view.findViewById<TextView>(R.id.countInformation)

        if(savedValue == -1){ // 오늘 최대값이 없다면, 오늘 최대값 설정하고, max값 지정, 그 외에는 그대로
            val maxTemp = sharedPreferences.getInt("smokeMaxCount", -1)
            val editor = sharedPreferences.edit()
            editor.putInt("smokeMaxCount"+date.getCurrentDate(), maxTemp)
            editor.apply()
            savedValue = maxTemp
        }

//        val editor = sharedPreferences.edit()
//        editor.putInt(date.getCurrentDate(), 0)
//        editor.apply() //오늘 횟수 리셋용 코드
        Log.d("date : ",date.getCurrentDate())

        if(savedValue == -1){
            information.text = "0/0"
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("일일 흡연 횟수를 설정 해주세요")
                .setMessage("프로필 -> 일일흡연 횟수에서 설정 가능합니다.")
            builder.show()
        }else{
            var maxCount = savedValue
            val date = DateParse()
            val savedValue = sharedPreferences.getInt(date.getCurrentDate(), 0)
            var toDayCount = maxCount - savedValue
            information.text = toDayCount.toString() + "/" + maxCount
        }
    }
    fun getAddress(lat: Double, lng: Double): List<Address>? {
        lateinit var address: List<Address>

        return try {
            val geocoder = Geocoder(requireContext(), Locale.KOREA)
            address = geocoder.getFromLocation(lat, lng, 1) as List<Address>
            address
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "주소를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun gpsCertification(lat: Double,lng: Double){
        // 흡연구역 지정이 필요해 보임
        var canSmoke = false;
        Log.d("text","${lat},${lng}")
        if(lat >= 37.4500700 && lat <= 37.4500800 && lng >= 126.6571 && lng <= 126.6572){
            canSmoke = true;
        }else if(lat >= 37.45062 && lat <= 37.45064 && lng >= 126.6566 && lng <= 126.6567){
            canSmoke = true;
        }else if(lat == 37.4219983 && lng == -122.084){
            Log.d("good","${lat},${lng}")
            canSmoke = true;
        }else{
            canSmoke = false;
        }
        val state = view.findViewById<TextView>(R.id.rangeState)
        if(canSmoke){
            state.text = "YES"
            state.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }else{
            state.text = "NO"
            state.setTextColor(ContextCompat.getColor(requireContext(), R.color.silver))
        }
    }

    fun gpsRenewBtnEvent(){
        val gpsToAddressBtn = view.findViewById<Button>(R.id.addressBtn)
        gpsToAddressBtn.setOnClickListener{
            gpsRenewEvent()
        }
    }

    @SuppressLint("MissingPermission")
    fun gpsRenewEvent(){
        val addressText = view.findViewById<TextView>(R.id.addressText)
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    val address = getAddress(location.latitude, location.longitude)?.get(0)
                    if (address != null) {
                        addressText.text = address.getAddressLine(0)
                        //addressText.text = (location.latitude.toString() + ","+ location.longitude.toString())
                    }
                    gpsCertification(location.latitude,location.longitude)
//                        addressText.text = address?.let { "${it.adminArea} ${it.locality} ${it.thoroughfare}"
//                        }
                }
            }
            .addOnFailureListener { fail ->
                Log.d("Failure : ","Calling address fail")
            }
    }
    fun smokeEvent(){
        //여기서 thread를 통해서 블루투스와의 통신
        initializeBluetooth() //연결 갱신으로 isConnected 활성화
        //임시코드임
        view.findViewById<LinearLayout>(R.id.smokeBtn).setOnClickListener{
                val date = DateParse()
                val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                val todayMax = sharedPreferences.getInt("smokeMaxCount"+date.getCurrentDate(), -1) //오늘의 최대값을 가져오기
                val todayCurrent = sharedPreferences.getInt(date.getCurrentDate(), -1)

                if(todayMax-todayCurrent == 0){
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("금일 가능한 횟수를 소진했습니다.")
                        .setMessage("프로필 -> 일일흡연 횟수에서 설정 가능합니다. (명일부터 적용됩니다)")
                    builder.show()
                }else{
                    runBlocking { initializeBluetooth() }  //비동기처리
                    if(isConnected){
                        runBlocking{ gpsRenewEvent() } //비동기처리
                        if(view.findViewById<TextView>(R.id.deviceState).text== "ON" && view.findViewById<TextView>(R.id.rangeState).text == "YES"){
                            val editor = sharedPreferences.edit()
                            editor.putInt(date.getCurrentDate(), todayCurrent+1)
                            editor.apply()

                            val todayNewCurrent = sharedPreferences.getInt(date.getCurrentDate(), -1)
                            view.findViewById<TextView>(R.id.countInformation).text = (todayMax-todayNewCurrent).toString()+"/"+todayMax.toString()
                            sendData(true)
                        }else{
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("흡연불가!")
                                .setMessage("흡연이 불가능한 위치이거나, 기기가 연결되어 있지 않습니다.")
                            builder.show()
                        }
                    }else{
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("연결된 기기없음")
                            .setMessage("휴대폰에 전자담배를 연결해주세요.")
                        builder.show()
                    }
                }
        }
    }

    private fun setupBluetoothConnection() {
        // Bluetooth 권한이 있는지 확인
        if (checkPermission(Manifest.permission.BLUETOOTH)) {
            // Bluetooth 권한이 있으면 초기화 진행
            initializeBluetooth()
        } else {
            // Bluetooth 권한이 없으면 권한 요청
            requestPermission(Manifest.permission.BLUETOOTH, PERMISSION_REQUEST_BLUETOOTH)
        }
    }

    private fun initializeBluetooth() {
        // Bluetooth 권한 확인

        //initialState
        val state = view.findViewById<TextView>(R.id.deviceState)
        state.text = "OFF"
        state.setTextColor(ContextCompat.getColor(requireContext(), R.color.silver))
        isConnected

        if (checkPermission(Manifest.permission.BLUETOOTH) && checkPermission(Manifest.permission.BLUETOOTH_ADMIN)) {
            // Bluetooth 활성화 확인
            if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
                // 페어링된 Bluetooth 디바이스 중에서 원하는 디바이스를 찾아 BluetoothSocket 설정
                val pairedDevices = bluetoothAdapter!!.bondedDevices
                if (pairedDevices.size > 0) {
                    for (device in pairedDevices) {
                        if (device.name == "CIG") { //

                            // 연결에 성공함
                            isConnected = true
                            state.text = "ON"
                            state.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                            try {
                                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                                bluetoothSocket!!.connect()
                                outputStream = bluetoothSocket!!.getOutputStream()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            break
                        }

                    }
                } else {
                    // 페어링된 장치가 없는 경우 처리
                    Log.d("BluetoothExample", "페어링된 장치가 없습니다.")
                    Toast.makeText(requireContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Bluetooth가 비활성화된 경우 처리
                Log.d("BluetoothExample", "Bluetooth is disabled!")
                Toast.makeText(requireContext(), "Bluetooth is disabled!", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Bluetooth 권한이 없는 경우 처리
            requestPermission(Manifest.permission.BLUETOOTH, PERMISSION_REQUEST_BLUETOOTH)
        }
    }

    private fun sendData(data: Boolean) {
// 기본 Toast 메시지 출력
        Toast.makeText(context, "${bluetoothAdapter},${bluetoothAdapter!!.isEnabled},${bluetoothSocket},${outputStream}", Toast.LENGTH_SHORT).show();
        if (bluetoothSocket != null && outputStream != null) {
            try {
                Log.d("??","true")
                //outputStream!!.write(data.toString().toByteArray())
                outputStream!!.write("yes".toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else{
            Log.d("??","false")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // Bluetooth가 성공적으로 활성화된 경우 Bluetooth 연결 설정
                setupBluetoothConnection()
            } else {
                // Bluetooth 활성화가 거부된 경우 처리
                // ...
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        // 권한이 있는지 확인
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        // 권한 요청
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_BLUETOOTH) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth 권한이 허용된 경우 초기화 진행
                setupBluetoothConnection()
            } else {
                // Bluetooth 권한이 거부된 경우 처리
                // 사용자에게 알림 등을 통해 권한이 필요하다고 알리고 추가적인 조치를 취함
                // ...
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 액티비티 종료 시 BluetoothSocket, OutputStream을 닫음
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket!!.close()
            }
            if (outputStream != null) {
                outputStream!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}