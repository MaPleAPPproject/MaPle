package group3.mypage;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.BufferUnderflowException;
import java.nio.charset.Charset;

public class CommonTask extends AsyncTask<String,Integer,String > {

    private final static String TAG = "CommonTask";
    private String url,outStr;


    public CommonTask(String url, String outStr) {
        this.url = url;
        this.outStr = outStr;
    }


    @Override
    protected String doInBackground(String... params) { return getRemoteData(); }

    private String getRemoteData() {
        HttpURLConnection cmConnection = null;
        StringBuilder inStr = new StringBuilder();
        try {
            cmConnection = (HttpURLConnection) new URL(url).openConnection();
            cmConnection.setDoInput(true);
            cmConnection.setDoOutput(true);
            cmConnection.setChunkedStreamingMode(0);
            cmConnection.setUseCaches(false);
            cmConnection.setRequestMethod("POST");
            cmConnection.setRequestProperty("charset","UTF-8");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(cmConnection.getOutputStream()));
            bw.write(outStr);
//            Log.d(TAG,"output:" + outStr);
            bw.close();

            int responseCode = cmConnection.getResponseCode();
            if(responseCode == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(cmConnection.getInputStream()));
                String line;
                while((line = br.readLine())!=null){
                    inStr.append(line);
                }
            }else{
                Log.d(TAG,"reponseCode:"+ responseCode);
            }
        }catch (IOException e){
            Log.e(TAG, e.toString());
        }finally {
            if(cmConnection!= null){
                cmConnection.disconnect();
            }
        }
//        Log.d(TAG, "input:"+inStr);
        return  inStr.toString();
    }
}
