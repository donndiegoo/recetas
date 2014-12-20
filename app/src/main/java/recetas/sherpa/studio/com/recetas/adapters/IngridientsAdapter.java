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
import android.widget.TextView;

import java.util.List;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;

/**
 * Created by diego on 15/12/14.
 */
public class IngridientsAdapter extends ArrayAdapter<String> {


    private final int  TYPE_INGRIDIENT = 1;
    private final int  TYPE_SECTION = 0;

    Context mContext;
    int mLayoutResourceId;

    public IngridientsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String ingridient = getItem(position);
        if(ingridient.startsWith(RecipeStepByStep.INGRIDIENT_STARTER))
        {
            return TYPE_INGRIDIENT;
        }
        else
        {
            return TYPE_SECTION;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(getItemViewType(position) == TYPE_INGRIDIENT)
        {
            return getViewIngridient(position, convertView,parent);
        }
        else
        {
            return getViewSection(position,convertView,parent);
        }
    }

    private View getViewSection(int position, View convertView, ViewGroup parent) {
        final ViewHolderSection holder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_ingridient_section, parent, false);
            holder = new ViewHolderSection();
            holder.textView = (TextView) convertView.findViewById(R.id.ingridients_section);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSection) convertView.getTag();
        }

        // object item based on the position
        String objectItem = getItem(position);

        holder.textView.setText(objectItem);

        return convertView;
    }

    private View getViewIngridient(int position, View convertView, ViewGroup parent) {
        final ViewHolderIngridient holder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolderIngridient();
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.ingridient_checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderIngridient) convertView.getTag();
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

    static class ViewHolderIngridient {
        CheckBox checkbox;
    }

    static class ViewHolderSection {
        TextView textView;
    }
}
