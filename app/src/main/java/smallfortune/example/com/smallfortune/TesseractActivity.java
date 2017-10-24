package smallfortune.example.com.smallfortune;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TesseractActivity extends AppCompatActivity {

    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = TesseractActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures";
    private TextView textView;
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        startCameraActivity();
    }

    //Método para checar as permissões de armazenamento.
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
    }

    //Metodo que inicializa o processo de captura da informação
    //contida na imagem.
    private void startCameraActivity(){
        try{
            //informa-se o diretório em que será salva a imagem.
            String imagePath = DATA_PATH+ "/imgs";
            File dir = new File(imagePath);
            if(!dir.exists()){
                //caso não exista é criado um novo diretório com o
                //respectivo caminho informado anteriormente.
                dir.mkdir();
            }
            //atribui-se a extensão da imagem ao caminho.
            String imageFilePath = imagePath+"/ocr.jpg";
            //Uri é um identificador de recursos
            outputFileDir = Uri.fromFile(new File(imageFilePath));
            //Intent é um objeto de mensagem que pode ser usado para
            // solicitar uma ação de outro componente do aplicativo.
            final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
            if(pictureIntent.resolveActivity(getPackageManager() ) != null){
                //Informa-se uma Intent e um código de requisição.
                // Trabalha em conjunto com a onActivityResult.
                startActivityForResult(pictureIntent, 1024);
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    //Como não se sabe se o usuario irá utilizar o recurso ou
    // se irá apenas cancelar a operação, este método trata de
    // verificar se o código informado na startActivityForResult
    // está correto.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024) {
            if (resultCode == Activity.RESULT_OK) {
                prepareTessData();
                startOCR(outputFileDir);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Método responsável por buscar os dados de linguagem
    // utilizados pelo Tesseract.
    private void prepareTessData(){
        try{
            File dir = new File(DATA_PATH + TESS_DATA);
            if(!dir.exists()){
                dir.mkdir();
            }
            String fileList[] = getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = DATA_PATH+TESS_DATA+"/"+fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //Método responsável por gerar o arquivo de imagem
    // contendo apenas a informação extraida da foto.
    private void startOCR(Uri imageUri){
        try{
            //Bitmap é uma matriz de bits que especifica a cor de cada pixel
            // numa matriz rectangular de pixeis.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 7;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),options);

            //Obtem-se os caracteres contidos na imagem e
            // informa-se por um intent o resultado.
            String resultado = this.getText(bitmap);
            Intent intent = new Intent();
            intent.putExtra("info",resultado);
            setResult(2,intent);
            //finaliza a activity.
            finish();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    //Método do Tesseract para extrair o texto da bitmap.
    private String getText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.init(DATA_PATH,"eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    //Verifica as permissões requisitadas e caso a aplicação não as tenha
    // como liberadas, é lançado um aviso de permissão negada.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 120:{
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Read permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 121:{
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Write permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
