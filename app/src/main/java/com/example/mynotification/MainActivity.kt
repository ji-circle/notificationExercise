package com.example.mynotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mynotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //notification버튼 누르면 함수 실행
        binding.notificationButton.setOnClickListener {
            notification()
        }
    }

    fun notification() {

        Log.d("Notification", "Notification function called")
        //notificationManager를 받아옴
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val builder: NotificationCompat.Builder
        //채널 만드는 게 8.0 이상 부터니까 버전체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 26 버전 이상

            // 추가한 코드... 사용자 권한 요청 부분. 설정으로 안내함
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    // 알림 권한이 없다면, 사용자에게 권한 요청
                    // Setting 는 android.provider 로 선택하기
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                    startActivity(intent)
                }
            }

            Log.d("Notification", "버전확인")

            val channelId = "one-channel"
            val channelName = "My Channel One"

            //채널을 하나 만든다. NotificationChannel로.
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {

                Log.d("Notification", "apply 부분")
                // 채널에 다양한 정보 설정
                description = "My Channel One Description"

                // 빨간색으로 1, 2 뜨는거...라는데 뭘까
                setShowBadge(true)

                //알람 울리게. 링톤
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                //오디오 들어가 있는거... 이건 기본 소리니까 바꾸고 싶으면 mp3 다운받아 바꾸기
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                //사운드에 오디오 넣기
                setSound(uri, audioAttributes)

                //진동 넣을건지
                enableVibration(true)
            }
            // 채널을 manager를 통해 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            // 채널을 이용하여 builder 생성, builder 통해 채널 아이디 넣어줌
            builder = NotificationCompat.Builder(this, channelId)

        } else {
            // 26 버전(8.0) 이하라면
            builder = NotificationCompat.Builder(this)
        }

        //사진 넣기
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.flower)

        //알림이 울렸을 때... 두 번째 액티비티 실행하는 코드
        val intent = Intent(this, SecondActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // 알림의 기본 정보... 빌더의 옵션들 ??
        builder.run {
            Log.d("Notification", "빌더 런")
            setSmallIcon(R.mipmap.ic_launcher)
            //알람 발생 시각 : 현재 시각
            setWhen(System.currentTimeMillis())
            setContentTitle("새로운 알림입니다.")
            setContentText("알림이 잘 보이시나요.")
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("이것은 긴텍스트 샘플입니다. 아주 긴 텍스트를 쓸때는 여기다 하면 됩니다.이것은 긴텍스트 샘플입니다. 아주 긴 텍스트를 쓸때는 여기다 하면 됩니다.이것은 긴텍스트 샘플입니다. 아주 긴 텍스트를 쓸때는 여기다 하면 됩니다.")
            )
            //라지아이콘에 사진 넣기
            setLargeIcon(bitmap)
            //위의 setStyle을 지우고... 이부분은 글 대신 사진을 크게 넣는 코드
//            setStyle(NotificationCompat.BigPictureStyle()
//                    .bigPicture(bitmap)
//                    .bigLargeIcon(null))  // hide largeIcon while expanding
            //걔를 눌렀을 때 펜딩인텐트(intent... secondActivity 호출하는...)
            addAction(R.mipmap.ic_launcher, "Action", pendingIntent)
        }

        //permission 추가해주기!
        //알림 실행
        manager.notify(11, builder.build())
        Log.d("Notification", "실행")
    }

}