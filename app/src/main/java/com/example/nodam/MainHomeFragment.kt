package com.example.nodam
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.mobilegpsexam.RequestPermissionsUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import java.io.IOException
import java.util.Locale

class MainHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 인플레이트하고 반환합니다.
        val view = inflater.inflate(R.layout.fragment_main_home, container, false)
        RequestPermissionsUtil(requireContext()).requestLocation()
        gpsRenewEvent(view)
        setRemainCount(view)
        gpsRenewBtnEvent(view)
        smokeEvent(view)
        return view
    }
    fun setRemainCount(view:View){
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
    fun gpsRenewBtnEvent(view:View){
        val gpsToAddressBtn = view.findViewById<Button>(R.id.addressBtn)
        gpsToAddressBtn.setOnClickListener{
            gpsRenewEvent(view)
        }
    }
    @SuppressLint("MissingPermission")
    fun gpsRenewEvent(view: View){
        val addressText = view.findViewById<TextView>(R.id.addressText)
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    val address = getAddress(location.latitude, location.longitude)?.get(0)
                    if (address != null) {
                        addressText.text = address.getAddressLine(0)

                    }
//                        addressText.text = address?.let { "${it.adminArea} ${it.locality} ${it.thoroughfare}"
//                        }
                }
            }
            .addOnFailureListener { fail ->
                Log.d("Failure : ","Calling address fail")
            }
    }
    fun smokeEvent(view:View){
        //여기서 thread를 통해서 블루투스와의 통신



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
                val editor = sharedPreferences.edit()
                editor.putInt(date.getCurrentDate(), todayCurrent+1)
                editor.apply()

                val todayNewCurrent = sharedPreferences.getInt(date.getCurrentDate(), -1)
                view.findViewById<TextView>(R.id.countInformation).text = (todayMax-todayNewCurrent).toString()+"/"+todayMax.toString()
            }
        }
    }
}