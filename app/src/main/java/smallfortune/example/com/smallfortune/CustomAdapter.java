package smallfortune.example.com.smallfortune;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smallfortune.example.com.smallfortune.R;

/**
 * Created by rafae on 10/10/2017.
 */

 //Um Adapter funciona como uma ponte entre uma AdapterView e os dados destinados para essa View.
public class CustomAdapter extends BaseAdapter {

    TextView descricao, valor, vencimento;
    Context context;
	
	//ArrayList é uma classe para coleções. Ao criar objetos através de uma classe é
	//possivel agrupá-los e realizar várias operações, como: 
	//adicionar e retirar elementos, ordená-los, procurar por um elemento específico, 
	//apagar um elemento específico, limpar o ArrayList dentre outras possibilidades.
    ArrayList<Data> data;
	
	//A classe LayoutInflater é usada para instanciar o layoutl do arquivo XML nos
	//objetos correspondentes da view.
    LayoutInflater inflater;

	//Método construtor, é chamado sempre que for instanciado um objeto desta classe.
    public CustomAdapter(Context context, ArrayList<Data> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

	//Método utilizado para criar uma ListView customizada.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.from(context).inflate(R.layout.custom_list,viewGroup,false);

        descricao = (TextView) view.findViewById(R.id.readdesc);
        valor = (TextView) view.findViewById(R.id.readvalue);
        vencimento = (TextView) view.findViewById(R.id.readdate);
        descricao.setText(descricao.getText()+data.get(i).getDescricao());
        valor.setText(valor.getText()+""+ data.get(i).getValor());
        vencimento.setText(vencimento.getText()+""+ data.get(i).getVencimento());

        return view;
    }
}