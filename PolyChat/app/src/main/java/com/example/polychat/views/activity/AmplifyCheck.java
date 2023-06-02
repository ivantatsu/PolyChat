package com.example.polychat.views.activity;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.pushnotifications.pinpoint.AWSPinpointPushNotificationsPlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;

public class AmplifyCheck extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        amplifyInit();
    }
    private void amplifyInit() {
        try{
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Log.i("Amplify Auth", "Cognito Inicializado");
        } catch (AmplifyException e) {
            Log.e("Amplify Auth", "No se ha podido inicializar Cognito AWS");
        }

        try{
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Log.i("Amplify S3", "S3 Inicializado");
        }catch(AmplifyException e) {
            Log.e("Amplify S3", "No se ha podido inicializar S3 AWS");
        }

        try{
            Amplify.addPlugin(new AWSApiPlugin());
            Log.i("Amplify API", "API Inicializado");
        }catch(AmplifyException e) {
            Log.e("Amplify API", "No se ha podido inicializar API AWS");
        }

        try{
            Amplify.configure(getApplicationContext());
            Log.i("Amplify AWS", "Amplify Inicializado");
        } catch (AmplifyException e) {
            Log.e("Amplify AWS", "No se ha podido inicializar Amplify AWS" + e.getMessage());
        }
    }

}
