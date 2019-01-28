package pt.ipleiria.estg.dei.amsi.fixbyte;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.amsi.fixbyte.listeners.ComprasListener;
import pt.ipleiria.estg.dei.amsi.fixbyte.listeners.FixByteListener;
import pt.ipleiria.estg.dei.amsi.fixbyte.modelo.Campanha;
import pt.ipleiria.estg.dei.amsi.fixbyte.modelo.Compra;
import pt.ipleiria.estg.dei.amsi.fixbyte.modelo.FixByteSingleton;
import pt.ipleiria.estg.dei.amsi.fixbyte.modelo.Produto;
import pt.ipleiria.estg.dei.amsi.fixbyte.utils.FixByteJsonParser;

public class ComprasItemActivity extends AppCompatActivity{

    public static final String IDPRODUTO = "IDPRODUTO";

    private ImageView imageViewProduto;
    private TextView textViewNome;
    private TextView textViewDescricao;
    private TextView textViewPrecoOriginal;
    private Button btn;

    private Produto produto;
    FloatingActionButton fab;
    long idProduto;
    private String ImageRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        idProduto = getIntent().getLongExtra(IDPRODUTO,-1);

        imageViewProduto = findViewById(R.id.imageViewProduto);
        textViewNome = findViewById(R.id.textViewNome);
        textViewDescricao = findViewById(R.id.textViewDescricao);
        textViewPrecoOriginal = findViewById(R.id.textViewPrecoOriginal);


        btn = findViewById(R.id.buttonAddCart);
        btn.setText("Delete");

        btn.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String Token = preferences.getString("token", "");
                FixByteSingleton.getInstance(getApplicationContext()).APIdeleteCompras(getApplicationContext(),FixByteJsonParser.isConnectedInternet(getApplicationContext()),Token,idProduto);
            }
        });

        produto = FixByteSingleton.getInstance(getApplicationContext()).getProduto(idProduto);
        preencherDadosProduto();


    }

    private void preencherDadosProduto()
    {
        textViewNome.setText(produto.getProdutoNome());
        textViewDescricao.setText(produto.getProdutoDescricao1()+"\n"+
                produto.getProdutoDescricao2()+"\n"+
                produto.getProdutoDescricao3()+"\n"+
                produto.getProdutoDescricao4()+"\n"+
                produto.getProdutoDescricao5()+"\n"+
                produto.getProdutoDescricao6()+"\n"+
                produto.getProdutoDescricao7()+"\n"+
                produto.getProdutoDescricao8()+"\n"+
                produto.getProdutoDescricao9()+"\n"+
                produto.getProdutoDescricao10());
        textViewPrecoOriginal.setText(produto.getProdutoPreco().toString()+"€");

        String IPAdress = FixByteSingleton.getInstance(getApplicationContext()).IPAdress;
        ImageRoute = "http://"+IPAdress+"/TeSP-PSI-PL2-07-WEB/frontend/web/images/products"+"/"+produto.getIdprodutos()+"/"+produto.getProdutoImagem1();

        Glide.with(getApplicationContext())
                .load(ImageRoute)
                .placeholder(R.drawable.logo_fixbyte_drop_shadow)
                .thumbnail(0f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewProduto);
    }
}
