package it.unive.dais.cevid.aac.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.aac.R;

/**
 * Created by Fonto on 11/09/17.
 */

public class SoldiPubbliciAdapter extends RecyclerView.Adapter<SoldiPubbliciAdapter.SoldiPubbliciItem> {
    private static final String TAG = "SoldiPubbliciAdapter";
    private List<EntitieExpenditure> dataList;
    private String capite;

    public SoldiPubbliciAdapter(List<EntitieExpenditure> dataList, String capite) {
        this.dataList = dataList;
        this.capite = capite;
    }

    @Override
    public SoldiPubbliciItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_public_expenditure, parent, false);
        return new SoldiPubbliciItem(itemView);

    }

    @Override
    public void onBindViewHolder(SoldiPubbliciItem holder, int position) {
        Double importo = Double.parseDouble(dataList.get(position).getImporto()) / 100;
        holder.voceSpesa.setText(dataList.get(position).getDescrizione_codice());
        holder.importo.setText(importo + "€");
        holder.procapite.setText(String.valueOf(importo / Double.parseDouble((capite))) + "€");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class SoldiPubbliciItem extends RecyclerView.ViewHolder{
        private TextView voceSpesa, importo, procapite;

        public SoldiPubbliciItem(View itemView) {
            super(itemView);
            voceSpesa = (TextView) itemView.findViewById(R.id.description_exp);
            importo = (TextView) itemView.findViewById(R.id.public_exp);
            procapite = (TextView) itemView.findViewById(R.id.pro_capite);
        }
    }
}
