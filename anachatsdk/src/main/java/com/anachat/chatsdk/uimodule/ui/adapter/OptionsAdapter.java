package com.anachat.chatsdk.uimodule.ui.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anachat.chatsdk.internal.model.MessageResponse;
import com.anachat.chatsdk.uimodule.utils.InputIntents;
import com.anachat.chatsdk.internal.database.PreferencesManager;
import com.anachat.chatsdk.internal.model.Message;
import com.anachat.chatsdk.internal.model.Option;
import com.anachat.chatsdk.internal.utils.constants.Constants;
import com.anachat.chatsdk.library.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyViewHolder> {

    private List<Option> optionList;
    private Context context;
    private Message message;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.messageText);
        }
    }


    public OptionsAdapter(Context context, Message message) {
        this.optionList = message.getMessageInput().getOptionsAsList();
        this.context = context;
        this.message = message;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_input_type_options, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        GradientDrawable drawable = (GradientDrawable) holder.itemView.getBackground();
        drawable.setStroke(3,
                Color.parseColor(PreferencesManager.getsInstance(context).getThemeColor()));
        holder.itemView.setBackground(drawable);
        final Option option = optionList.get(position);
        holder.title.setText(option.getTitle());
        holder.itemView.setOnClickListener(view -> {
            String value = option.getValue();
            if (option.getType() == 0) {
                try {
                    JSONObject jsonObject = new JSONObject(option.getValue());
                    if (jsonObject.has("url")) {
                        value = jsonObject.getString("value");
                        context.startActivity(
                                InputIntents.getBrowserIntent(jsonObject.getString("url")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            message.getMessageInput().setMandatory(Constants.FCMConstants.MANDATORY_TRUE);
            MessageResponse.MessageResponseBuilder responseBuilder
                    = new MessageResponse.MessageResponseBuilder
                    (context.getApplicationContext().getApplicationContext());
            MessageResponse messageResponse = responseBuilder.inputTextString(value,
                    message)
                    .build();
            messageResponse.getData().getContent().getInput().setOptionText(option.getTitle());
            messageResponse.send();
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }
}