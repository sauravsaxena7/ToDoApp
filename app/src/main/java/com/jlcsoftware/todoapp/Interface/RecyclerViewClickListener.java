package com.jlcsoftware.todoapp.Interface;

public interface RecyclerViewClickListener {

    void onItemClicked(int position);

    void onLongItemClick(int position);

    void onEditButtonClick(int position);

    void onDeleteButtonClick(int position);

    void onDoneButtonClick(int position);

}
