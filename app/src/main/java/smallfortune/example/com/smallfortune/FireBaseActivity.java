package smallfortune.example.com.smallfortune;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

import smallfortune.example.com.smallfortune.R;

public class FireBaseActivity extends Activity {

    LayoutInflater inflater1;
    int count =0;
    double valor;
    String descricao;
    String vencimento;
    String key;
    EditText txtdesc, txtvalue, txtdate, txtsearch;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Data data;
    ListView listView;
    ArrayList<Data> dataArrayList;
    CustomAdapter customAdapter;
    int temp;

    private static final String TAG = "FireBaseActivity";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);
        mDisplayDate = (TextView) findViewById(R.id.writedate);
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Nesse campo é informado o nome de referência do banco que será usado.
        databaseReference = firebaseDatabase.getReference().child("Meus Gastos");
        key=databaseReference.push().getKey();

        txtdesc = (EditText) findViewById(R.id.writedesc);
        txtvalue = (EditText)findViewById(R.id.writevalue);
        txtdate = (EditText)findViewById(R.id.writedate);
        listView = (ListView) findViewById(R.id.readlist);

		//Um listener é normalmente implementado em forma de interface.
		//Ele serve para escutar o que acontece em um objeto e avisar a outro.
        findViewById(R.id.btn_tesseract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FireBaseActivity.this, TesseractActivity.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
            }
        });

        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //O Toast fornece um feedback simples e rápido ao usuário em uma pequena janela.
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    descricao = txtdesc.getText().toString().trim();

                    if (TextUtils.isEmpty(descricao) ) {

                        Toast.makeText(getApplicationContext(), "Informe uma Descrição", Toast.LENGTH_SHORT).show();

                    } else {

                        valor = Double.parseDouble(txtvalue.getText().toString().trim());
                        vencimento = txtdate.getText().toString().trim();

                        data = new Data(databaseReference.push().getKey(), descricao, valor, vencimento);
                        databaseReference.child(data.getKey()).setValue(data);
                        Toast.makeText(getApplicationContext(), "Cadastro Realizado com Sucesso", Toast.LENGTH_SHORT).show();

                        txtdesc.setText("");
                        txtvalue.setText("");
                        txtdate.setText("");
                    }
                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dataArrayList = new ArrayList<>();
        customAdapter = new CustomAdapter(FireBaseActivity.this, dataArrayList);
        listView.setAdapter(customAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Data datam = dataSnapshot.getValue(Data.class);
                dataArrayList.add(datam);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Método que chama o calendario ao clicar no campo vencimento
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        FireBaseActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //Define o listener quando o usuário define a data
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Dentro do método getView da classe CustomAdapter, se faz necessário utilizar o
                // método inflate para transformar o xml do layout em uma view.
                final View v = inflater1.from(getApplicationContext()).inflate(R.layout.custom_alert, null);
                temp = i;

                final EditText updtdesc, updtvalue, updtdate;

                updtdesc = (EditText) v.findViewById(R.id.updtdesc);
                updtvalue = (EditText) v.findViewById(R.id.updtvalue);
                updtdate = (EditText) v.findViewById(R.id.updtdate);

                final AlertDialog.Builder builder  = new AlertDialog.Builder(FireBaseActivity.this).setView(v);
                final AlertDialog alert = builder.create();

                v.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Data tempData = new Data(dataArrayList.get(temp).getKey(), updtdesc.getText().toString().trim(),
                                Double.parseDouble(updtvalue.getText().toString().trim()), updtdate.getText().toString().trim());

                        databaseReference.child(dataArrayList.get(temp).getKey()).setValue(tempData);
                        dataArrayList.remove(temp);
                        dataArrayList.add(temp,tempData);
                        customAdapter.notifyDataSetChanged();
                    }
                });

                v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(temp == -1){

                            Toast.makeText(getApplicationContext(), "Não há nada a ser excluido", Toast.LENGTH_SHORT).show();

                        }else {

                            databaseReference.child(dataArrayList.get(temp).getKey()).removeValue();
                            dataArrayList.remove(temp);
                            customAdapter.notifyDataSetChanged();
                            alert.cancel();
                            temp = -1;
                        }
                    }
                });

                updtdesc.setText(dataArrayList.get(temp).getDescricao());
                updtvalue.setText("" + dataArrayList.get(temp).getValor());
                updtdate.setText(dataArrayList.get(temp).getVencimento());

                try {

                    alert.show();

                } catch (Exception e) {

                    Log.d("show", "onItemClick: " + e);
                }

                return;
            }
        });

        txtsearch = (EditText) findViewById(R.id.search);

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Retorna uma nova cadeia de caracteres na qual todas
                // as ocorrências à esquerda e à direita são removidas.
                descricao = txtsearch.getText().toString().trim();

                databaseReference.orderByChild("descricao").equalTo(descricao).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ++count;

                        //DataSnapshot é um instantâneo dos dados. Um instantâneo é uma imagem dos dados
                        // em uma referência de banco de dados específica de um determinado momento.
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            data = dataSnapshot1.getValue(Data.class);
                            dataArrayList.clear();
                            dataArrayList.add(data);
                            Log.d("log", "onDataChange: "+dataSnapshot1.child("descricao").getValue());
                        }

                        func();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        realtimeUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Verifica se o código de requisão é o mesmo que foi passado.
        if (requestCode == 2) {

            String retorno = data.getStringExtra("info");
            txtvalue.setText(retorno);
        }
    }

    public void realtimeUpdate(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    data = dataSnapshot1.getValue(Data.class);
                    dataArrayList.add(data);
                }

                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void func(){

        if(count!=0){

            customAdapter = new CustomAdapter(getApplicationContext(),dataArrayList);
            listView.setAdapter(customAdapter);

        }else {

            Toast.makeText(getApplicationContext(), "There is no data to show", Toast.LENGTH_SHORT).show();
            listView.setVisibility(View.GONE);
        }
    }
}