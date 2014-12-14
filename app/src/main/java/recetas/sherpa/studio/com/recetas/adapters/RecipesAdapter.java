package recetas.sherpa.studio.com.recetas.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.Recipe;

/**
 * Created by diego on 11/12/14.
 */
public class RecipesAdapter extends ArrayAdapter<Recipe> {

    Context mContext;
    int layoutResourceId;
    Recipe data[] = null;

    public RecipesAdapter(Context mContext, int layoutResourceId, Recipe[] data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.info_image);
            holder.text = (TextView) convertView.findViewById(R.id.info_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // object item based on the position
        Recipe objectItem = data[position];

        holder.text.setText(objectItem.getTitle());

        String picturePath = objectItem.getFirstPicture();
        if(picturePath.length() > 0)
        {
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(mContext)
                    .load(new File(objectItem.getFirstPicture()))
                    .resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
                    .centerInside()
                    .tag(mContext)
                    .into(holder.image);
        }



        return convertView;

    }

    static class ViewHolder {
        ImageView image;
        TextView text;
    }
}
