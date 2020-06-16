package blackdots.t.punnubchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import blackdots.t.punnubchat.R;
import blackdots.t.punnubchat.model.StateModel;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {

    List<StateModel> stateModels =new ArrayList<>();
    Context context;

    public StateAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(stateModels.get(position).getSender());
        holder.tv_message.setText(stateModels.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return stateModels.size();
    }

    public void removeUser(StateModel sm) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.tv_name);
            tv_message=itemView.findViewById(R.id.tv_message);
        }
    }

    public void addMessage(StateModel stateModel){
        stateModels.add(stateModel);
        notifyDataSetChanged();
    }
}
