package com.example.goldenromance.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.goldenromance.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards> {

    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> item) {
        super(context, resourceId, item);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        cards card_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView budget = (TextView) convertView.findViewById(R.id.budget);
        ImageView mGiveImage = (ImageView) convertView.findViewById(R.id.giveImage);
        ImageView mNeedImage = (ImageView) convertView.findViewById(R.id.needImage);

        name.setText(card_item.getName());
        budget.setText(card_item.getBudget());


        if (card_item.getNeed().equals("Petanca")) {
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.petanca));
        } else if (card_item.getNeed().equals("Telenovelas"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.telenovelas));
        else if (card_item.getNeed().equals("Leer"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.leer));
        else if (card_item.getNeed().equals("Bailar"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bailar));
        else if (card_item.getNeed().equals("Bingo"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bingo));
        else
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.none));

        if (card_item.getGive().equals("Petanca"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.petanca));
        else if (card_item.getGive().equals("Telenovelas"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.telenovelas));
        else if (card_item.getGive().equals("Leer"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.leer));
        else if (card_item.getGive().equals("Bailar"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bailar));
        else if (card_item.getGive().equals("Bingo"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bingo));
        else
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.none));


        switch (card_item.getProfileImageUrl()) {
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.profile).into(image);
            break;
            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }
        return convertView;
    }


}
