package com.example.ddmxm.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ArrayAdapter<String> mForecastAdapter;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //Создаём фейковые данные для Listview. Данные представляют собой погоду на всю неделю.
            String[] data ={
                    "Mon - 11/7 - Sunny - 32/16",
                    "Tue - 12/7 - Rainy - 21/8",
                    "Wed - 13/7 - Foggy - 23/17",
                    "Thu - 14/7 - Sunny - 18/11",
                    "Fri - 15/7 - Sunny - 20/13",
                    "Sat - 16/7 - Cloudy - 34/10",
                    "San - 17/7 - Sunny - 37/10"
            };
            List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

            //Теперь с созданными вручную фейковыми данными создаю ArrayAdapter
            //ArrayAdapter связывается с ListView
            mForecastAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    weekForecast);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //Передаю значение на ListView и прикрепляю к адаптеру
            ListView listView =(ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForecastAdapter);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Хранит в себе JSON ответ, как String
            String forecastJsonStr = null;

            try {
                //Создаём URL для запроса в openweathermap.org
                //API описан на странице http://openweathermap.org/api
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Moscow,ru&cnt=7&unit=metric&mode=json";
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));



                //Создаём запрос к OpenWeatherMap и открываем соединение
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Читаем входящий поток как String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Поток пустой.  Нечего парсить.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // Если код код не может успешно забрать данные о погоде, тогда нет смысла парсить это
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }




            return rootView;
        }
    }
}
