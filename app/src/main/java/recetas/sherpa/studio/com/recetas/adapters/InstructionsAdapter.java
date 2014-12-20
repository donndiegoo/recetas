package recetas.sherpa.studio.com.recetas.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;

/**
 * Created by diego on 15/12/14.
 */
public class InstructionsAdapter extends ArrayAdapter<String> {


    private final int  TYPE_INSTRUCTION = 1;
    private final int  TYPE_SECTION = 0;

    Context mContext;
    int mLayoutResourceId;

    public InstructionsAdapter(Context context, int resource, List<String> objects) {
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
            return TYPE_INSTRUCTION;
        }
        else
        {
            return TYPE_SECTION;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(getItemViewType(position) == TYPE_INSTRUCTION)
        {
            return getViewInstruction(position, convertView, parent);
        }
        else
        {
            return getViewSection(position,convertView,parent);
        }
    }

    private View getViewSection(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_instructions_sections, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.instructions_section);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // object item based on the position
        String objectItem = getItem(position);

        holder.textView.setText(objectItem);

        return convertView;
    }

    private View getViewInstruction(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.instructions_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // object item based on the position
        String objectItem = getItem(position);

        holder.textView.setText(objectItem.substring(1).trim());

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
