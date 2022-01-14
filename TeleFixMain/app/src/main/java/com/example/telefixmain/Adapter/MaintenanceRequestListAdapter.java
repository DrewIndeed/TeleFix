package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MaintenanceRequestListAdapter extends RecyclerView.Adapter<MaintenanceRequestItemViewHolder> {
    private FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mechanicId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    private boolean isDeny = false;

    Context activityContext;
    ArrayList<MaintenanceRequest> maintenanceRequests;

    public MaintenanceRequestListAdapter(Context activityContext, ArrayList<MaintenanceRequest> maintenanceRequests) {
        this.activityContext = activityContext;
        this.maintenanceRequests = maintenanceRequests;
    }

    @NonNull
    @Override
    public MaintenanceRequestItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_dialog_maintenance_booking, parent, false);
        return new MaintenanceRequestItemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MaintenanceRequestItemViewHolder holder, int position) {
        holder.actionButtons.setVisibility(View.GONE);

        String userId = maintenanceRequests.get(position).getUserId();
        long dtValue = maintenanceRequests.get(position).getDatetime();
        String dtString = timeConverter(dtValue);
        String[] dtArray = dtString.split("\\s");

        // get customer info (name + phone) and set text on mechanic screen
        ArrayList<User> tmp = new ArrayList<>();
        DatabaseHandler.getSingleUser(db, userId, tmp, () -> {
            holder.title.setText(tmp.get(0).getName() + "\n" + tmp.get(0).getPhone());
        });

        holder.status.setVisibility(View.VISIBLE);
        holder.status.setText("Waiting for acceptance");

        holder.date.setText(dtArray[0]);
        holder.time.setText(dtArray[1]);

        holder.date.setEnabled(false);
        holder.time.setEnabled(false);

        holder.dialog.setOnClickListener(view -> {
            String requestId = maintenanceRequests.get(position).getRequestId();
            String vendorId = maintenanceRequests.get(position).getVendorId();
            // Open respond dialog
            openRespondDialog(vendorId, requestId, dtArray);








//            AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
//            builder.setTitle("Accept Maintenance booking request");
//            builder.setMessage("Click \"Confirm\" to accept your task. \"Deny\" if ");
//            builder.setPositiveButton("Confirm", (dialog, id)
//                    -> BookingHandler.respondMaintenanceRequest(vendorBookings,
//                    activityContext,
//                    vendorId,
//                    requestId,
//                    mechanicId,
//                    "accepted"));
//            builder.setNegativeButton("Deny", (dialog, id)
//                    -> BookingHandler.respondMaintenanceRequest(vendorBookings,
//                    activityContext,
//                    vendorId,
//                    requestId,
//                    mechanicId,
//                    "rejected"));
//            AlertDialog alert = builder.create();
//            alert.show();
        });
    }

    private void openRespondDialog(String vendorId, String requestId, String[] time) {
        CustomProgressDialog respondDialog = new CustomProgressDialog(activityContext, R.style.SheetDialog, R.layout.progress_dialog_maintenance_booking);
        View root = respondDialog.getDialogRootView();

        // Bind element
        LinearLayout linearLayout = root.findViewById(R.id.ll_datetime_info);
        TextView title = root.findViewById(R.id.tv_maintenance_title);
        TextView status = root.findViewById(R.id.tv_maintenance_status);
        EditText respond = root.findViewById(R.id.tv_maintenance_response);
        Button denyBtn = root.findViewById(R.id.btn_cancel_maintenance);
        Button acceptBtn = root.findViewById(R.id.btn_confirm_maintenance_user);

        // Set display info
        title.setText("Confirm Maintenance Request");
        linearLayout.setVisibility(View.GONE);
        status.setVisibility(View.VISIBLE);
        status.setText("Confirm appointment on date: " + time[0] + " at " + time[1] + "?");

        // Change text of button
        denyBtn.setText("Deny");
        acceptBtn.setText("Confirm");

        // Bind action
        denyBtn.setOnClickListener(view -> {
            isDeny = !isDeny;
            if (isDeny) {
                respond.setVisibility(View.VISIBLE);
                denyBtn.setText("Cancel Deny");
                acceptBtn.setText("Deny and send reason");
                acceptBtn.setBackgroundResource(R.drawable.custom_btn_bg_17);
            }
            else {
                respond.getText().clear();
                respond.setVisibility(View.GONE);
                denyBtn.setText("Deny");
                acceptBtn.setText("Confirm Request");
                acceptBtn.setBackgroundResource(R.drawable.custom_btn_bg_8);
            }

        });

        acceptBtn.setOnClickListener(view -> {
            if (isDeny) {
                BookingHandler.respondMaintenanceRequest(vendorBookings,activityContext,
                        vendorId,
                        requestId,
                        respond.getText().toString(),
                        "rejected");
            }
            else {
                BookingHandler.respondMaintenanceRequest(vendorBookings,activityContext,
                        vendorId,
                        requestId,
                        mechanicId,
                        "accepted");
            }
            respondDialog.dismiss();
        });

        respondDialog.show();
    }

    @Override
    public int getItemCount() {
        if (maintenanceRequests != null) {
            return maintenanceRequests.size();
        }
        return 0;
    }

    /**
     * Method to convert the unix timestamp to GMT
     */

    public static String timeConverter(long unixValue) {
        Date date = new java.util.Date(unixValue * 1000L);
        // the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyy HH:mm");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        return sdf.format(date);
    }
}
