package com.example.lanco.mobile_sms.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lanco.mobile_sms.DB.DBCalendarManager;
import com.example.lanco.mobile_sms.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;



public class SettingActivity extends Fragment {
    LoginButton facebookLogin;//페이스북 로그인 버튼
    LoginManager loginManager;//페이스북 로그인 매니져
    String birthday = "", name = "", tmp = "";//디비에 저장되어질 생일 이름
    CallbackManager callbackManager;
    Button getBtn;
    AccessToken accessToken;//페이스북에 접근 할 수 있는 토큰
    DBCalendarManager db;

    ///////////////////////////////
    Switch toggleSwitch ;
    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences2;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;
    RadioGroup setSound;
/////////////////////////////////////

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        getBtn = (Button)view.findViewById(R.id.get);



        ////////////////////////
        sharedpreferences =this.getActivity().getSharedPreferences("getPosition", Context.MODE_PRIVATE);
        sharedpreferences2 =this.getActivity().getSharedPreferences("getP", Context.MODE_PRIVATE);

        editor = sharedpreferences.edit();
        editor2 = sharedpreferences2.edit();
        toggleSwitch = (Switch)view.findViewById(R.id.toggleSwitch);

        //sound setting
        setSound = (RadioGroup)view.findViewById(R.id.setSound);
        RadioButton rd = (RadioButton)view.findViewById(setSound.getCheckedRadioButtonId());
        RadioButton sAndV=(RadioButton)view.findViewById(R.id.sAndV);
        RadioButton vive=(RadioButton)view.findViewById(R.id.vive);
        RadioButton noSound=(RadioButton)view.findViewById(R.id.noSound);

        //sound detail setting visibility and initialization
        if(sharedpreferences.getInt("num",1)==1){

            toggleSwitch.setChecked(true);

            setSound.setVisibility(View.VISIBLE);

        }


        if(sharedpreferences.getInt("num",0)==0){

            toggleSwitch.setChecked(false);
            setSound.setVisibility(View.INVISIBLE);


        }
        if(sharedpreferences2.getInt("set",1)==1){

            sAndV.setChecked(true);
        }
        else if(sharedpreferences2.getInt("set",2)==2){

            vive.setChecked(true);
        }
        else if(sharedpreferences2.getInt("set",3)==3){

            noSound.setChecked(true);
        }

//alarm setting on/off
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // TODO Auto-generated method stub
                //on click
                if (isChecked) {
                    editor.putInt("num", 1);
                    setSound.setVisibility(View.VISIBLE);
                }
                //off click
                else {
                    editor.putInt("num", 0);
                    setSound.setVisibility(View.INVISIBLE);

                }
                editor.commit();

            }
        });

        //alarm detail setting on/off
        setSound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {                 // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.sAndV:
                        editor2.putInt("set", 1);
                        break;
                    case R.id.vive:
                        editor2.putInt("set", 2);
                        break;
                    case R.id.noSound:
                        editor2.putInt("set", 3);
                        break;
                    default:
                        break;
                }

                editor2.commit();
            }
        });
        ;

        ///////////////////////

        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) view.findViewById(R.id.login_button);
        facebookLogin.setReadPermissions("user_friends,user_birthday");//facebook 권한요청
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {//로그인시 실행
            @Override
            public void onSuccess(final LoginResult loginResult) {//페이스북 로그인 성공시
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(//graphapi 요청
                                            JSONObject object, GraphResponse response) {
                    }
                });
                accessToken = loginResult.getAccessToken();//로그인 성공시 토큰을 전역변수로 저장
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");//graph api에 쿼리문 보내기
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        accessToken = AccessToken.getCurrentAccessToken();

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//친구 생일 받아오기 버튼 눌렀을 시
                loginManager.getInstance().logInWithReadPermissions(SettingActivity.this, Arrays.asList("user_friends,user_birthday"));
                GraphRequest request = GraphRequest.newMeRequest(accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(//graphapi 요청
                                                    JSONObject object, GraphResponse response) {
                                // Application code
                                if (accessToken != null) {
                                    try {//친구의 생일이 있을 시 데이터 베이스에 저장
                                        Cursor c;
                                        db = new DBCalendarManager(getContext());
                                        db.open(); c = db.getAllContacts();
                                        int check = 0;
                                        JSONObject friends;
                                        friends = response.getJSONObject().getJSONObject("friends");
                                        JSONArray data = friends.getJSONArray("data");
                                        for(int i = 0; i < data.length(); i++) {
                                            JSONObject object1 = data.getJSONObject(i);
                                            name = object1.getString("name");
                                            tmp = object1.getString("birthday");
                                            birthday = tmp.substring(0,5);
                                            if(c.moveToFirst()) {
                                                do {
                                                    if (c.getString(1).equals(name)){
                                                        check = 1;
                                                        break;
                                                    }
                                                } while(c.moveToNext());
                                            }
                                            if(check == 0)
                                                db.insertContact(name,birthday);
                                            check = 0;
                                        }
                                        db.close();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        //Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "friends{name,birthday}");
                request.setParameters(parameters);
                request.executeAsync();
            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}