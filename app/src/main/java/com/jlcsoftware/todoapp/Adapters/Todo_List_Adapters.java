package com.jlcsoftware.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jlcsoftware.todoapp.Interface.RecyclerViewClickListener;
import com.jlcsoftware.todoapp.Model.TodoModel;
import com.jlcsoftware.todoapp.R;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class Todo_List_Adapters extends RecyclerView.Adapter<Todo_List_Adapters.MyViewHolder> {

    ArrayList<TodoModel> arrayList;
    Context context;

    final private RecyclerViewClickListener clickListener;

    public Todo_List_Adapters(Context context, ArrayList<TodoModel> arrayList ,RecyclerViewClickListener clickListener) {
        this.arrayList=arrayList;
        this.context=context;
        this.clickListener=clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item,parent,false);

        final MyViewHolder myViewHolder= new MyViewHolder(view);



        int[] androidColors = view.getResources().getIntArray(R.array.androidcolors);
        int randomColors = androidColors[new Random().nextInt(androidColors.length)];

        myViewHolder.accordian_title.setBackgroundColor(randomColors);

        myViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myViewHolder.accordian_body.getVisibility()==View.VISIBLE){
                    myViewHolder.accordian_body.setVisibility(View.GONE);
                }else{
                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
                }
            }
        });



        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Todo_List_Adapters.MyViewHolder holder, int position) {

        final String title = arrayList.get(position).getTitle();
        final String description = arrayList.get(position).getDescription();
        final String id = arrayList.get(position).getId();

        if(arrayList.get(position).isFinished()){
            holder.done_btn.setVisibility(View.GONE);
            holder.edit_btn.setVisibility(View.GONE);
            holder.delete_btn.setVisibility(View.VISIBLE);
        }else{
            holder.done_btn.setVisibility(View.VISIBLE);
            holder.edit_btn.setVisibility(View.VISIBLE);
            holder.delete_btn.setVisibility(View.VISIBLE);
        }

        holder.title_tv.setText(title);
        if(!description.equals("")){
            holder.description_tv.setText(description);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView accordian_title ;
        TextView title_tv,description_tv;
        RelativeLayout accordian_body;
        ImageView done_btn,edit_btn,delete_btn,arrow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title_tv=itemView.findViewById(R.id.task_title);
            description_tv=itemView.findViewById(R.id.task_description);
            accordian_title = itemView.findViewById(R.id.accordian_title);
            accordian_body=itemView.findViewById(R.id.accordian_body);

            done_btn=itemView.findViewById(R.id.done_btn);
            edit_btn=itemView.findViewById(R.id.edit_btn);
            delete_btn=itemView.findViewById(R.id.delete_btn);

            arrow=itemView.findViewById(R.id.arrow);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clickListener.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });


            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onEditButtonClick(getAdapterPosition());
                }
            });

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onDeleteButtonClick(getAdapterPosition());
                }
            });

            done_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onDoneButtonClick(getAdapterPosition());
                }
            });


        }
    }
}
