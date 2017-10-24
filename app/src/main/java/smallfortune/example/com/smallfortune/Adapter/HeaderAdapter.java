package smallfortune.example.com.smallfortune.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import smallfortune.example.com.smallfortune.R;

/**
 * Created by Gustavo on 25/09/2017.
 */

public class HeaderAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;


    public HeaderAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);

        this.inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
  public View getView(int position, View convertView, ViewGroup parent) {
      View layoutHeader = inflater.inflate(R.layout.navigation_drawer_header, null);
      TextView txtUsuario = (TextView) layoutHeader.findViewById(R.id.usuarioTxt);
      TextView txtEmail = (TextView) layoutHeader.findViewById(R.id.emailTxt);
        txtUsuario.setText("teste");

      return layoutHeader;
    }

}




