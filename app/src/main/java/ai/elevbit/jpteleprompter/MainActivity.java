/*
 * JP Teleprompter
 * Criado por Joaquim Pedro de Morais Filho - j360074@hotmail.com
 * (C) 2026 - Todos os direitos reservados.
 *
 * Tela principal: o usuario digita/cola o roteiro, ajusta tamanho da
 * fonte e opacidade, e inicia o teleprompter que flutua sobre o WhatsApp.
 */
package ai.elevbit.jpteleprompter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String PREFS = "jpteleprompter";
    public static final String K_TEXT = "texto";
    public static final String K_SIZE = "tamanho";
    public static final String K_ALPHA = "opacidade";

    private EditText edRoteiro;
    private SeekBar sbTamanho, sbOpacidade;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        edRoteiro = findViewById(R.id.edRoteiro);
        sbTamanho = findViewById(R.id.sbTamanho);
        sbOpacidade = findViewById(R.id.sbOpacidade);

        edRoteiro.setText(prefs.getString(K_TEXT, getString(R.string.roteiro_exemplo)));
        sbTamanho.setProgress(prefs.getInt(K_SIZE, 30));
        sbOpacidade.setProgress(prefs.getInt(K_ALPHA, 70));

        Button btIniciar = findViewById(R.id.btIniciar);
        Button btParar = findViewById(R.id.btParar);
        Button btWhats = findViewById(R.id.btWhats);

        btIniciar.setOnClickListener(v -> iniciarTeleprompter());
        btParar.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            Toast.makeText(this, R.string.msg_parado, Toast.LENGTH_SHORT).show();
        });
        btWhats.setOnClickListener(v -> abrirWhatsApp());
    }

    private void salvar() {
        prefs.edit()
                .putString(K_TEXT, edRoteiro.getText().toString())
                .putInt(K_SIZE, sbTamanho.getProgress())
                .putInt(K_ALPHA, sbOpacidade.getProgress())
                .apply();
    }

    private void iniciarTeleprompter() {
        salvar();

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.msg_permissao, Toast.LENGTH_LONG).show();
            Intent p = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(p);
            return;
        }

        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        String texto = edRoteiro.getText().toString();
        if (texto.trim().isEmpty()) {
            Toast.makeText(this, R.string.msg_vazio, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, OverlayService.class);
        i.putExtra(OverlayService.EXTRA_TEXT, texto);
        i.putExtra(OverlayService.EXTRA_SIZE, 16 + sbTamanho.getProgress()); // 16..116 sp
        i.putExtra(OverlayService.EXTRA_ALPHA, (int) (sbOpacidade.getProgress() * 2.55)); // 0..255
        startForegroundService(i);

        Toast.makeText(this, R.string.msg_iniciado, Toast.LENGTH_LONG).show();
        moveTaskToBack(true);
    }

    private void abrirWhatsApp() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        if (i == null) i = getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b");
        if (i != null) {
            startActivity(i);
        } else {
            Toast.makeText(this, R.string.msg_sem_whats, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        salvar();
    }
}
