package com.example.bharat.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsURLs = new ArrayList<String>();
    ArrayList<String> celebsName = new ArrayList<String>();
    int chooseCelebs = 0;

    int locationOfCorrectAns = 0;
    String[] answer = new String [4];

    ImageView imageView;
    Button button0, button1, button2, button3;

    public void celebChoosen(View view){

        if(view.getTag().equals(Integer.toString(locationOfCorrectAns))){

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebsName.get(chooseCelebs), Toast.LENGTH_SHORT).show();
        }

        creatNewQuestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url ;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int data = inputStreamReader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return  result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void creatNewQuestion() {
        Random random = new Random();
        chooseCelebs = random.nextInt(celebsURLs.size());

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage;

        try {

            celebImage = imageTask.execute(celebsURLs.get(chooseCelebs)).get();


            imageView.setImageBitmap(celebImage);

            locationOfCorrectAns= random.nextInt(4);
            int inCorrectLocation;
            for(int i= 0 ; i<4 ; i++){
                if(i==locationOfCorrectAns){

                    answer[i] = celebsName.get(chooseCelebs);

                }else
                {
                    inCorrectLocation = random.nextInt(celebsURLs.size());

                    while (inCorrectLocation==chooseCelebs){

                        inCorrectLocation = random.nextInt(celebsURLs.size());

                    }
                    answer[i] = celebsName.get(inCorrectLocation);
                }
            }

            button0.setText(answer[0]);
            button1.setText(answer[1]);
            button2.setText(answer[2]);
            button3.setText(answer[3]);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button0);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebsURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()){
                celebsName.add(m.group(1));

            }

            creatNewQuestion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }



}
