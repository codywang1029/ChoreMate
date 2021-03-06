package model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kwang66.edu.choremate.CalendarViewFragment;
import kwang66.edu.choremate.CompleteChoreFragment;
import kwang66.edu.choremate.MainActivity;
import kwang66.edu.choremate.NotificationFragment;
import kwang66.edu.choremate.R;
import kwang66.edu.choremate.ViewCompletedFragment;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<Notification> notifications;
    Context mContext;

    public NotificationAdapter(List<Notification> notifications, Context mContext) {
        this.notifications = notifications;
        this.mContext = mContext;
    }

    public static class MyViewHolder extends ViewHolder {
        private final ImageView avatar;
        private final TextView name;
        private final TextView time;
        private final TextView msg;
        private final TextView action;
        //get each view from layout
        public MyViewHolder(View v) {
            super(v);
            avatar = v.findViewById(R.id.notification_avatar);
            name = v.findViewById(R.id.notification_name);
            time = v.findViewById(R.id.notification_time);
            msg = v.findViewById(R.id.notification_msg);
            action = v.findViewById(R.id.notification_action);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        //TODO
        Notification curr = notifications.get(position);
        holder.avatar.setImageResource(curr.getAvatar());
        holder.name.setText(curr.getName());
        holder.msg.setText(curr.getMessage());
        holder.time.setText(curr.getTime());
        holder.action.setText(curr.getAction());

        if (curr.getAction()!=null && curr.getAction().equals("view")){
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity activity = (MainActivity)mContext;
                    FragmentTransaction ft  = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_frame, new ViewCompletedFragment());
                    ft.commit();
                }
            });
        }
        if (curr.getAction()!=null && curr.getAction().equals("detail")){
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MainActivity activity = (MainActivity) mContext;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            activity,
                            R.style.AlertDialogTheme);

                    builder.setTitle("Buy Coke");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        builder.setMessage(Html.fromHtml(summaryBuilder(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        builder.setMessage(Html.fromHtml(summaryBuilder()));
                    }
                    builder.setCancelable(true);

                    builder.setPositiveButton("Accept Chore!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // create chore object and add to chore list
                            Chore createdChore = new Chore(
                                    "Buy Coke", "12/06/2018", "07:00 PM",
                                    UserManager.getInstance().getUser("John"), 2);

                            ChoreManager.getInstance().chores.add(createdChore);

                            NotificationManager notificationManager = NotificationManager.getInstance();
                            String createdDate = new SimpleDateFormat("HH:mm MM/dd").format(new Date());
                            Notification note = new Notification(R.drawable.john,"John",createdDate,"John has accepted a chore ("+createdChore.getChoreName()+") created by Dez",null);
                            notificationManager.notifications.add(0,note);
                            notifications.remove(position+1);
                            notifyItemRemoved(position+1);
                            notifyItemRangeChanged(position+1, getItemCount());
                            // go to calendar view
                            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.main_frame, new CalendarViewFragment());
                            ft.commit();
                        }
                    });

                    builder.setNegativeButton("Reject Chore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Chore createdChore = new Chore(
                                    "Buy Coke", "12/06/2018", "07:00 PM",
                                    UserManager.getInstance().getUser("John"), 2);
                            NotificationManager notificationManager = NotificationManager.getInstance();
                            String createdDate = new SimpleDateFormat("HH:mm MM/dd").format(new Date());
                            Notification note = new Notification(R.drawable.john,"John",createdDate,"John has rejected a chore ("+createdChore.getChoreName()+") created by Dez. They need to meet in person.",null);
                            notificationManager.notifications.add(0,note);
                            notifications.remove(position+1);
                            notifyItemRemoved(position+1);
                            notifyItemRangeChanged(position+1, getItemCount());
                            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.main_frame, new NotificationFragment());
                            ft.commit();
                            dialog.cancel();
                        }
                    });

                    AlertDialog choreSummary = builder.create();
                    choreSummary.show();
                }
            });
        }


    }

    private String summaryBuilder() {
        String smallBreak = "<br>";

        String review = "Dez tasked you with a chore! Review its settings before accepting it:<br><br>";
        String deadline = "<b><i>Due By: </i></b>" + "12/06/2018 at 07:00 PM<br>";
        String difficulty = "<b><i>Chore Difficulty: </i></b>" + "Easy<br>";
        String cost = "<b><i>Chore Cost: </i></b>" + "$2<br>";

        return review + smallBreak + deadline + smallBreak + difficulty + smallBreak + cost;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
