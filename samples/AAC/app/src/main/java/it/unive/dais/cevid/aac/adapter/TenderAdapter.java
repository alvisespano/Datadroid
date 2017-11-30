package it.unive.dais.cevid.aac.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.parser.ParticipantParser;

/**
 * Created by fbusolin on 16/11/17.
 */

public class TenderAdapter extends RecyclerView.Adapter<TenderAdapter.PartecipationItem> {
    private List<ParticipantParser.Data> data;

    public TenderAdapter(List<ParticipantParser.Data> data){
        this.data = data;
    }
    @Override
    public PartecipationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_tender, parent, false);
        return new PartecipationItem(itemView);
    }

    @Override
    public void onBindViewHolder(PartecipationItem holder, int position) {
        holder.esito.setText(data.get(position).esito);
        holder.iva.setText(data.get(position).piva);
        holder.nome.setText(data.get(position).nome_partecipante);
        holder.lotto.setText(data.get(position).nome_lotto);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PartecipationItem extends RecyclerView.ViewHolder{
        TextView iva,esito,lotto,nome;

        public PartecipationItem(View itemView) {
            super(itemView);
            iva = (TextView) itemView.findViewById(R.id.text_vat);
            nome = (TextView) itemView.findViewById(R.id.text_part_nome);
            esito = (TextView) itemView.findViewById(R.id.text_result);
            lotto = (TextView) itemView.findViewById(R.id.text_plot);
        }
    }
}
