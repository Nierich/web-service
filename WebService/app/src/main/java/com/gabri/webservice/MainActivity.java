package com.gabri.webservice;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private EditText etCnpjValidar = null;
    private TextView tvCnpjDetalhes = null;
    private WebView myWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCnpjValidar = (EditText) findViewById(R.id.et_cnpj_validar);
        tvCnpjDetalhes = (TextView) findViewById(R.id.tv_cnpj_detalhes);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        myWebView = (WebView) findViewById(R.id.mywebview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);

        Button btBotaoUrl = (Button) findViewById(R.id.bt_botao_url);
        btBotaoUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://servicos.receita.fazenda.gov.br/Servicos/CPF/ConsultaSituacao/ConsultaPublica.asp";
                myWebView.loadUrl(url);
            }
        });
    }

    public <HttpURLConnection> void validaCnpj(View view) throws RuntimeException {
        String siteUrl = "https://www.receitaws.com.br/v1/cnpj/" + etCnpjValidar.getText();
        String contudoJSON = "";

        BufferedReader bufferReader = null;
        try {
            try {
                URL url = new URL(siteUrl);
                BufferedInputStream inputStream = new BufferedInputStream(url.openConnection().getInputStream());
                bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            } catch (Exception e) {
                e.printStackTrace();
            }

            contudoJSON = BufferParaString(bufferReader);

        } catch (Exception e) {
            Log.e("Log error", "Não foi possível conectar: " + e.getMessage());

            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        try {
            JSONObject jObj = new JSONObject(contudoJSON);
            tvCnpjDetalhes.setText("Situação do cnpj: " + jObj.get("situacao") + "" +
                    "\nRazão social: " + jObj.get("nome") + "" +
                    "\nFantasia: " + jObj.get("fantasia") +
                    "\nEndereço: " + jObj.get("logradouro") + " ," + jObj.get("numero") + " , " + jObj.get("bairro") + " , " + jObj.get("municipio"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String BufferParaString(BufferedReader reader) {
        String linha;
        StringBuffer buffer = new StringBuffer();
        try {
            while ((linha = reader.readLine()) != null) {
                buffer.append(linha);
                buffer.append("\n");
            }

            return buffer.toString();
        } catch (Exception e) {
            Log.e("Erro:", "Erro durante a conversão do buffer para string:" + e.getMessage());
            return "";
        }
    }
}