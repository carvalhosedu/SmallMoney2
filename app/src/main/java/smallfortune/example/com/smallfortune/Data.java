package smallfortune.example.com.smallfortune;

/**
 * Created by rafae on 10/10/2017.
 */

public class Data {

    String key;
    String descricao;
    double valor;
    String vencimento;

	//Método construtor, é chamado sempre que for instanciado um objeto desta classe.
    public Data(String key, String descricao, double valor, String vencimento) {
        this.key = key;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
    }

    public Data() {
    }

    public String getKey() {
        return key;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public String getVencimento() {
        return vencimento;
    }
}