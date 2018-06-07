package cmps121.phonote;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class AutoCitationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    static public String BIB_URL;
    static final public String LOG_TAG = "Web_View";

    WebView webView;
    JSONObject json = new JSONObject();
    boolean retrieved = false;
    boolean sent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_citation);

        BIB_URL = "http://www.bibme.org/mla/book-citation/search?utf8=%E2%9C%93&q=";

        final Button auto_source = (Button) findViewById(R.id.auto_source_btn);
        final Button manual_source = (Button) findViewById(R.id.manual_source_btn);
        final TextView auto_prompt_text = (TextView) findViewById(R.id.prompt_textView);
        final TextView manual_prompt_text = (TextView) findViewById(R.id.manual_prompt_textView);
        final EditText search_title = (EditText) findViewById(R.id.search_title_editText);

        final TextView genView = (TextView) findViewById(R.id.gen_textView);
        final ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("name");

        webView = (WebView) findViewById(R.id.web_view);
        webView.setVisibility(View.GONE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        genView.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);

        retrieved = false;
        sent = false;

        auto_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText searchTitle = (EditText) findViewById(R.id.search_title_editText);
                String searchString = searchTitle.getText().toString();
                Scanner sc = new Scanner(searchString);
                StringBuilder temp = new StringBuilder();
                while (sc.hasNext()) {
                    temp.append(sc.next());
                    if (sc.hasNext()) {
                        temp.append("+");
                    }
                }
                BIB_URL += temp.toString();
                Log.d(LOG_TAG, BIB_URL);
                webView.loadUrl(BIB_URL);
                auto_source.setVisibility(View.GONE);
                manual_source.setVisibility(View.GONE);
                auto_prompt_text.setVisibility(View.GONE);
                manual_prompt_text.setVisibility(View.GONE);
                search_title.setVisibility(View.GONE);

                genView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

        manual_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createSourceIntent = new Intent(AutoCitationActivity.this, CreateSourceActivity.class);
                createSourceIntent.putExtra("name", name);
                createSourceIntent.putExtra("title", "");
                createSourceIntent.putExtra("author", "");
                createSourceIntent.putExtra("publisher", "");
                createSourceIntent.putExtra("city", "");
                createSourceIntent.putExtra("year", "");
                startActivity(createSourceIntent);
                finish();
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("");
                String javascript = "(function() {" +
                        "var x = document.querySelector('#search-results > div > div > div.col-md-4.col-xs-3 > form > input[name=item_json]').value;" +
                        "return x;" +
                        "})()";
                view.evaluateJavascript(javascript, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        if (!retrieved && !sent) {
                            try {
                                value = value.substring(value.indexOf('{'), value.lastIndexOf('}') + 1);
                                value = value.replaceAll("\\\\", "");
                                Log.d(LOG_TAG, value);
                                json = new JSONObject(value);
                                retrieved = true;
                            } catch (JSONException je) {
                                Log.d(LOG_TAG, je.getMessage());
                                Context context = getApplicationContext();
                                CharSequence text = "No match found";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } catch (StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Context context = getApplicationContext();
                                CharSequence text = "No match found";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            Intent createSourceIntent = new Intent(AutoCitationActivity.this, CreateSourceActivity.class);
                            if (retrieved) {
                                try {
                                    createSourceIntent.putExtra("name", name);
                                    createSourceIntent.putExtra("title", json.getJSONObject("pubnonperiodical").getString("title"));
                                    Log.d("JSON", json.getJSONObject("pubnonperiodical").getString("title"));
                                    try {
                                        JSONObject author = json.getJSONArray("contributors").getJSONObject(0);
                                        createSourceIntent.putExtra("author", (author.getString("last").equals("") ? "" : (author.getString("last") + ", ")) +
                                                (author.getString("first").equals("") ? "" : (author.getString("first") + " ")) +
                                                (author.getString("middle").equals("") ? "" : (" " + author.getString("middle"))));
                                        Log.d("JSON", (author.getString("last").equals("") ? "" : (author.getString("last") + ", ")) +
                                                (author.getString("first").equals("") ? "" : (author.getString("first"))) +
                                                (author.getString("middle").equals("") ? "" : (" " + author.getString("middle"))));
                                    } catch (Exception e) {
                                        createSourceIntent.putExtra("author", "");
                                    }

                                    createSourceIntent.putExtra("publisher", json.getJSONObject("pubnonperiodical").getString("publisher"));
                                    Log.d("JSON", json.getJSONObject("pubnonperiodical").getString("publisher"));
                                    createSourceIntent.putExtra("city", json.getJSONObject("pubnonperiodical").getString("city"));
                                    Log.d("JSON", json.getJSONObject("pubnonperiodical").getString("city"));
                                    createSourceIntent.putExtra("year", json.getJSONObject("pubnonperiodical").getString("year"));
                                    Log.d("JSON", json.getJSONObject("pubnonperiodical").getString("year"));
                                } catch (JSONException je) {
                                    je.printStackTrace();
                                    Log.e("JSON", je.getMessage());
                                }
                            } else {
                                createSourceIntent.putExtra("name", name);
                                createSourceIntent.putExtra("title", "");
                                createSourceIntent.putExtra("author", "");
                                createSourceIntent.putExtra("publisher", "");
                                createSourceIntent.putExtra("city", "");
                                createSourceIntent.putExtra("year", "");
                            }
                            sent = true;
                            startActivity(createSourceIntent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
