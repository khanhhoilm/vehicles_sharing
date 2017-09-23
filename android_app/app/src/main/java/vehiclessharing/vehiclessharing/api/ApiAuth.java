package vehiclessharing.vehiclessharing.api;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vehiclessharing.vehiclessharing.VSharing;
import vehiclessharing.vehiclessharing.utils.LocalApiURL;

/**
 * Created by Hihihehe on 9/10/2017.
 */

public class ApiAuth {
    public interface AuthCallbackInterface {

        //  public void resolveCommentData(List<CommentBlock> commentBlock);

        public void register(String message);

        public void registerSentResult(String message);
        public void signInResult(String message);

        public void errorEncountered(String message);
    }

    private AuthCallbackInterface mCallback;

    public ApiAuth(AuthCallbackInterface callback) {
        this.mCallback = callback;
    }

    public void registerWithPhoneNumber(String phone_number, String name, String password,
                                        String gender) {
        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs
                    .add(new BasicNameValuePair("phone", phone_number));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("gender", gender));
            HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);

            CallPostApi api = new CallPostApi() {
                @Override
                protected void onPostExecute(String result) {
                    // TODO Auto-generated method stub
                    if (android.os.Debug.isDebuggerConnected())
                        android.os.Debug.waitForDebugger();
                    Log.d("Call API", result);
                  //  if(Integer.parseInt(result)==1) {
                        mCallback.registerSentResult(result);
                   // }
                    super.onPostExecute(result);
                }
            };
            api.setHeader("application/x-www-form-urlencoded");
            api.setEntity(entity);

            api.execute(LocalApiURL.signUpURL);

            requestTimeOut(api);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

    }
    public void signInWithPhoneNumber(String phone_number, String password) {
        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs
                    .add(new BasicNameValuePair("phone", phone_number));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);

            CallPostApi api = new CallPostApi() {
                @Override
                protected void onPostExecute(String result) {
                    // TODO Auto-generated method stub
                    if (android.os.Debug.isDebuggerConnected())
                        android.os.Debug.waitForDebugger();
                    Log.d("Call API", result);
                    //  if(Integer.parseInt(result)==1) {
                    mCallback.signInResult(result);
                    // }
                    super.onPostExecute(result);
                }
            };
            api.setHeader("application/x-www-form-urlencoded");
            api.setEntity(entity);

            api.execute(LocalApiURL.signinURL);

            requestTimeOut(api);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

    }
    private void requestTimeOut(final AsyncTask api) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.getStatus() == AsyncTask.Status.RUNNING) {
                    api.cancel(true);

                    mCallback.errorEncountered("Lỗi kết nối mạng! Chạm để thử lại");
                }
            }
        }, 10000);
    }

    private void requestTimeOut(final CallPostApi api) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.getStatus() == AsyncTask.Status.RUNNING) {
                    api.cancel(true);

                    mCallback.errorEncountered("Lỗi kết nối mạng !");
                }
            }
        }, 10000);
    }

    public static void writeStringAsFile(final String fileContents,
                                         String fileName) {

        // String fileName = "bookmarks.txt";
        Context context = VSharing.mContext;
        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(),
                    fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileAsString(String fileName) {

        // String fileName = "bookmarks.txt";

        Context context = VSharing.mContext;
        Log.d("TT Context", context.toString());
        //Context context=getApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(
                    context.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null)
                stringBuilder.append(line);
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

  /*  public static boolean commentLiked(String id, List<String> commentLikedList) {

        try {

            // String data =
            // readFileAsString(GlobalConstant.commentLikedFileName);
            // Log.i("LIKED COMMENT", data);
            //
            // Log.i("INPUT ARTICLE", id + " ");
            //
            // Gson gson = new Gson();
            // Type listType = new TypeToken<List<String>>() {
            // }.getType();
            //
            // List<String> commentLikedList = gson.fromJson(data, listType);

            if (commentLikedList != null) {
                for (String likedComment : commentLikedList) {
                    Log.i("COMMENT", likedComment + " " + id);
                    if (likedComment.equals(id)) {
                        Log.i("COMMENT EQUALS", likedComment + " " + id);
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<String> setLikedComment(String likedComment) {

        List<String> commentLikedList = null;

        try {

            String data = readFileAsString(GlobalConstant.commentLikedFileName);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            commentLikedList = gson.fromJson(data, listType);

            if (commentLikedList == null)
                commentLikedList = new ArrayList<String>();

            if (!commentLikedList.contains(likedComment))
                commentLikedList.add(likedComment);
            else
                commentLikedList.remove(likedComment);

            data = gson.toJson(commentLikedList, listType);

            writeStringAsFile(data, GlobalConstant.commentLikedFileName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return commentLikedList;

    }*/
}
