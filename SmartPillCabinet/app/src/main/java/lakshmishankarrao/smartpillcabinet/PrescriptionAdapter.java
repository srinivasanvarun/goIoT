package lakshmishankarrao.smartpillcabinet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lakshmi on 3/12/2016.
 */
public class PrescriptionAdapter extends ArrayAdapter<Prescription> {
    final List<Prescription> prescriptions;
    private Context ctxt;
    public PrescriptionAdapter(Context context, int resource, List<Prescription> objects) {
        super(context, resource, objects);
        this.prescriptions = objects;
        ctxt = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ScrapViewHolder holder;
        View row = convertView;
        if (convertView == null) {
            // This is an expensive operation! Avoid and reuse as much as possible.
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.prescription_custom_row, parent, false);
            holder = new ScrapViewHolder();
            holder.label = (TextView) row.findViewById(R.id.textViewMedicineName);


            row.setTag(holder);
        }
        else{
            //Log.d("lux", "Row NOT null; reusing it!");
            holder = (ScrapViewHolder) row.getTag();
        }

        holder.label.setText(prescriptions.get(position).getName());

        return row;
    }

    public class ScrapViewHolder {
        TextView label;
        ImageView icon;
        ImageView imageView;
    }
}
