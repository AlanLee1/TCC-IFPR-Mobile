package com.ifpr.projeto.tcc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifpr.projeto.tcc.R;
import com.ifpr.projeto.tcc.model.Incidente;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Incidente> listaIncidentes;

    public Adapter(List<Incidente> lista) {
        this.listaIncidentes = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.incidente_detalhe,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Incidente incidente = listaIncidentes.get(position);
        holder.tipo.setText(incidente.getTipo());
        holder.bairro.setText(incidente.getBairro());
        holder.logradouro.setText(incidente.getLogradouro());
        holder.descricao.setText(incidente.getDescricao());
        String urlImagem = incidente.getCaminhoImagem();
        if(urlImagem != null){
            Picasso.get().load( urlImagem ).into( holder.imagem );
        }else{
            holder.imagem.setImageResource(R.drawable.image);
        }

    }

    @Override
    public int getItemCount() {
        return listaIncidentes.size();
    }

    //Responsavel por guardar cada um dos dados antes de ser exibido na tela
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tipo;
        TextView bairro;
        TextView logradouro;
        TextView descricao;
        ImageView imagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tipo = itemView.findViewById(R.id.txtTipo);
            bairro = itemView.findViewById(R.id.txtBairro);
            logradouro = itemView.findViewById(R.id.txtLogradouro);
            descricao = itemView.findViewById(R.id.txtDescricao);
            imagem = itemView.findViewById(R.id.imageIncidente);

        }
    }
}
