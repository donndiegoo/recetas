package recetas.sherpa.studio.com.recetas.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import recetas.sherpa.studio.com.recetas.R;

/**
 * Created by diego on 15/12/14.
 */
public class IngridientsAdapter extends ArrayAdapter<String> {

    Context mContext;
    int mLayoutResourceId;

    public IngridientsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.ingridient_checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // object item based on the position
        String objectItem = getItem(position);

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    holder.checkbox.setPaintFlags(holder.checkbox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    holder.checkbox.setPaintFlags(holder.checkbox.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        holder.checkbox.setText(objectItem);

        return convertView;
    }

    static class ViewHolder {
        CheckBox checkbox;
        int normalPaintFlags;
    }
}
