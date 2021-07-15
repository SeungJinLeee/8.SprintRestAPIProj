package com.kosmo.fcmmessagingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//https://firebase.google.com/docs/cloud-messaging/android/receive?hl=ko
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG="fcm_messaging_app";

    //※포그라운드 상태인 앱에서도 알림 메시지(FCM에서 자동처리)수신하려면
    // onMessageReceived 콜백 오버라이딩

    //파이어베이스 콘솔에서 알림 메시지 및 데이타 메시지를 보낼때
    //포그라운드 일때:모든 경우 onMessageRecieved가 호출됨
    //백그라운드일때:데이타메시지를 포함한 경우에만 호출된다
    @Override
    public void onMessageReceived(@NonNull  RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG,"From:"+remoteMessage.getFrom());
        //알림메시지:키값이 정해져 있다 :제목-title,내용-body 예:{
        // "notification":{"title":"알림 제목","body":"알림 텍스트"}}
        if(remoteMessage.getNotification() !=null){
            Log.i(TAG,"알림 제목:"+remoteMessage.getNotification().getTitle());
            Log.i(TAG,"알림 텍스트:"+remoteMessage.getNotification().getBody());
        }
        //데이타 메시지(추가 옵션인 키/값항목에 입력한 데이타)
        //getData():Map컬렉션 반환
        //데이타 메시지가 있는 경우
        //포그라운드일때:알림도 데이타 메시지로 변경
        //백그라운드 일때:노티는 알림메시지가 뜨고 데이터 메시지는 MainActivity의 인텐트 부가 정보로 전송
        if(remoteMessage.getData().size() > 0){
            Log.i(TAG,"데이타 메시지:"+remoteMessage.getData());
            Log.i(TAG,"데이타 메시지 제목:"+remoteMessage.getData().get("dataTitle"));
            Log.i(TAG,"데이타 메시지 텍스트:"+remoteMessage.getData().get("dataBody"));
            showNotification(remoteMessage.getData().get("dataTitle"),remoteMessage.getData().get("dataBody"));
        }
        else{//데이타 메시지가 없는 경우
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }

    }//////////////
    //토큰이 변경될때마다 호출됨.
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //설정에서 앱의 데이타 삭제후 LOGCAT확인
        //I/com.kosmo.iotpush: FCM token: cPBGRr06NwI:APA91bEqbqd9lO4mM_S0qhuRzmp8nMWrxgUnaxBBQ8bwwemdyWBCzUAOiifnJa1XTWJ7qG1JlRXQihqqN54oC2rDeugtfdHpCxDi4sqCGp4oQRTt9IgFJb-F3TAmu96-n5NoGkW0sMMs
        Log.i(TAG,"FCM token:"+token);
        //생성 등록된 토큰을 내 서버에 보내기.
        sendNewTokenToMyServer(token);

    }
    //아래는 내가 만든 웹 서비스(UI)와 연동하기 위한 코드들
    private void sendNewTokenToMyServer(String token) {
        Retrofit retrofit= new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("http://192.168.0.25:9090/")
                .build();
        TokenService tokenService=retrofit.create(TokenService.class);
        Call<String> call=tokenService.token(token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Log.i(TAG,"서버 응답 성공:"+response.body());
                }
                else{
                    Log.i(TAG,"서버 응답 실패:"+response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG,"서버 전송 실패:"+t.getMessage());
            }
        });
    }//////////////////sendNewTokenToMyServer
    private void showNotification(String title, String body) {
        Intent intent = new Intent(this,MoveActivity.class);
        //인텐트에 부가정보 저장
        intent.putExtra("dataTitle",title);
        intent.putExtra("dataBody",body);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"com.kosmo.myfcmapp.service")
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        //InboxStyle스타일 추가-여러줄의 body 입력시 표시하기 위함
        //한줄 짜리 body 입력시는 생략 가능
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        StringTokenizer tokenizer = new StringTokenizer(body,"\r\n");
        while(tokenizer.hasMoreTokens()){
            inboxStyle.addLine(tokenizer.nextToken());
        }
        builder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //오레오 부터 아래 코드 추가해야 함 시작
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("com.kosmo.myfcmapp.service","CHANNEL_NAME",importance);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,500});
        notificationManager.createNotificationChannel(channel);
        //오레오 부터 아래 코드 추가해야 함 끝
        notificationManager.notify(101,builder.build());
    }


}
