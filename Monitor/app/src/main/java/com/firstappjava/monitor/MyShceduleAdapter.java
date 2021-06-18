package com.firstappjava.monitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyShceduleAdapter extends ArrayAdapter<_Job> {
    public MyShceduleAdapter(@NonNull Context context, ArrayList<_Job> al) {
        super(context, 0, al);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }
        return networkInfo!=null && networkInfo.isConnected();
    }
    static class ViewHolder {
        public TextView tm;
        public TextView mark;
        public TextView type;
        public ImageView modify;
    }
    @SuppressLint({"SetTextI18n", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newView=convertView;
        if(newView==null) {
            LayoutInflater li=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            newView=li.inflate(R.layout.shcedule_one_job, parent, false);
            ViewHolder vh=new ViewHolder();
            vh.tm=newView.findViewById(R.id.Time);
            vh.mark=newView.findViewById(R.id.mark);
            vh.type=newView.findViewById(R.id.TypeOfJob);
            vh.modify=newView.findViewById(R.id.modify);

            newView.setTag(vh);
        }
        ViewHolder vh=(ViewHolder)newView.getTag();
        Job jb=getItem(position).getVal();

        String Date=ManageShcedule.getDayOrWeek()==0?"":jb.getDate().toString()+"\n";
        if(jb.Typeof().equals("Science")) {
           Science sc=(Science)jb;
           vh.tm.setText( Date+sc.getStartTime().toString()+"-"+
                          sc.getStartTime().Add(sc.getDurationMinute()).toString()); // (Science)
           vh.mark.setText(sc.getComment().trim().isEmpty()?"No comment":sc.getComment());
           vh.type.setText("Seance");
            if(isNetworkAvailable()) {
                if(sc.isDone()) {
                    vh.modify.setVisibility(View.INVISIBLE);
                }else {
                    vh.modify.setVisibility(View.VISIBLE);
                }
            }else {
                vh.modify.setVisibility(View.INVISIBLE);
            }
           vh.modify.setOnClickListener((View)-> {
               Intent intent=new Intent(parent.getContext(), modify_task_or_seance.class);

               intent.putExtra("Type", "Seance");
               intent.putExtra("SeanceId", sc.getScienceId());
               intent.putExtra("MonitorId", sc.getMonitorId());
               intent.putExtra("ClientId", sc.getClientId());
               intent.putExtra("Comment", sc.getComment());
               intent.putExtra("isDone", sc.isDone());

               parent.getContext().startActivity(intent);
           });
        }else if(jb.Typeof().equals("Task")) {
            Task ts=(Task)jb;
            vh.tm.setText( Date+ts.getStartTime().toString()+"-"+
                    ts.getStartTime().Add(ts.getDurationMinute()).toString()); // /*(Task)*/
            vh.mark.setText(ts.getTitle().trim().isEmpty()?"No title":ts.getTitle());
            vh.type.setText("   Task   ");
            if(isNetworkAvailable()) {
                if(ts.isDone) {
                    vh.modify.setVisibility(View.INVISIBLE);
                }else {
                    vh.modify.setVisibility(View.VISIBLE);
                }
            }else {
                vh.modify.setVisibility(View.INVISIBLE);
            }
            vh.modify.setOnClickListener((View)-> {
                Intent intent=new Intent(parent.getContext(), modify_task_or_seance.class);

                intent.putExtra("Type", "Task");
                intent.putExtra("TaskId", ts.getTaskID());
                intent.putExtra("MonitorId", ts.getUser_Fk());
                intent.putExtra("Detail", ts.getDetail());
                intent.putExtra("isDone", ts.isDone);

                parent.getContext().startActivity(intent);
            });
        }
        return newView;
    }
}
