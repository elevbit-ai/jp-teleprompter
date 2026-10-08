/*
 * JP Teleprompter - OverlayService
 * Criado por Joaquim Pedro de Morais Filho - j360074@hotmail.com
 *
 * Mostra um painel translucido flutuante (teleprompter) por cima de
 * qualquer app - inclusive a camera do WhatsApp. O texto e rolado
 * MANUALMENTE com o dedo. O painel ocupa a metade superior da tela,
 * deixando o botao de gravar do WhatsApp livre embaixo.
 */
package ai.elevbit.jpteleprompter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class OverlayService extends Service {

    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_SIZE = "size";
    public static final String EXTRA_ALPHA = "alpha";

    private static final String CH_ID = "jp_teleprompter";
    private static final int NOTIF_ID = 42;

    private WindowManager wm;
    private View root;
    private ScrollView scroll;
    private TextView tvRoteiro;
    private WindowManager.LayoutParams params;

    private float fonteSp = 30f;
    private int bgAlpha = 180;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            fonteSp = intent.getIntExtra(EXTRA_SIZE, 30);
            bgAlpha = intent.getIntExtra(EXTRA_ALPHA, 180);
        }
        iniciarForeground();
        if (root == null) {
            construirOverlay(intent);
        } else if (intent != null) {
            aplicarTexto(intent.getStringExtra(EXTRA_TEXT));
            aplicarFonte();
            aplicarFundo();
        }
        return START_STICKY;
    }

    private void iniciarForeground() {
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= 26 && nm.getNotificationChannel(CH_ID) == null) {
            NotificationChannel ch = new NotificationChannel(
                    CH_ID, "JP Teleprompter", NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("Teleprompter flutuante ativo");
            nm.createNotificationChannel(ch);
        }
        Intent volta = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, volta,
                PendingIntent.FLAG_IMMUTABLE);
        Notification n = new Notification.Builder(this, CH_ID)
                .setContentTitle("JP Teleprompter ativo")
                .setContentText("Abra o WhatsApp e grave. Toque para voltar ao app.")
                .setSmallIcon(R.drawable.ic_note)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
        startForeground(NOTIF_ID, n);
    }

    private void construirOverlay(Intent intent) {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        root = LayoutInflater.from(this).inflate(R.layout.overlay, null);

        scroll = root.findViewById(R.id.scroll);
        tvRoteiro = root.findViewById(R.id.tvRoteiro);
        View barra = root.findViewById(R.id.barra);

        aplicarTexto(intent != null ? intent.getStringExtra(EXTRA_TEXT) : "");
        aplicarFonte();
        aplicarFundo();

        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int altura = (int) (dm.heightPixels * 0.5);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                altura,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;

        // Arrastar pela barra de titulo
        barra.setOnTouchListener(new View.OnTouchListener() {
            int ix, iy;
            float tx, ty;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        ix = params.x; iy = params.y;
                        tx = e.getRawX(); ty = e.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = ix + (int) (e.getRawX() - tx);
                        params.y = iy + (int) (e.getRawY() - ty);
                        wm.updateViewLayout(root, params);
                        return true;
                }
                return false;
            }
        });

        ((Button) root.findViewById(R.id.btMais)).setOnClickListener(v -> {
            fonteSp = Math.min(fonteSp + 3, 120); aplicarFonte();
        });
        ((Button) root.findViewById(R.id.btMenos)).setOnClickListener(v -> {
            fonteSp = Math.max(fonteSp - 3, 12); aplicarFonte();
        });
        ((Button) root.findViewById(R.id.btOpMais)).setOnClickListener(v -> {
            bgAlpha = Math.min(bgAlpha + 25, 255); aplicarFundo();
        });
        ((Button) root.findViewById(R.id.btOpMenos)).setOnClickListener(v -> {
            bgAlpha = Math.max(bgAlpha - 25, 0); aplicarFundo();
        });
        ((Button) root.findViewById(R.id.btTopo)).setOnClickListener(v ->
                scroll.smoothScrollTo(0, 0));
        ((Button) root.findViewById(R.id.btFechar)).setOnClickListener(v -> stopSelf());

        wm.addView(root, params);
    }

    private void aplicarTexto(String t) {
        if (t == null) t = "";
        tvRoteiro.setText(t);
    }

    private void aplicarFonte() {
        tvRoteiro.setTextSize(TypedValue.COMPLEX_UNIT_SP, fonteSp);
    }

    private void aplicarFundo() {
        if (scroll != null) scroll.setBackgroundColor(Color.argb(bgAlpha, 0, 0, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wm != null && root != null) {
            try { wm.removeView(root); } catch (Exception ignored) {}
            root = null;
        }
    }
}
