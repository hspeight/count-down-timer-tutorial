package com.androidtutorialshub.countdowntimer.Data;

import java.io.File;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidtutorialshub.countdowntimer.Activities.R;
import com.androidtutorialshub.countdowntimer.Model.Backup;

import pub.devrel.easypermissions.EasyPermissions;

public class BackupListAdapter extends RecyclerView.Adapter<BackupListAdapter.ViewHolder>
                                        implements EasyPermissions.PermissionCallbacks {

    OnBackupDeleted mCallback;
    // Container Activity must implement this interface
    public interface OnBackupDeleted {
        void youDeletedIt(int position);
    }

    private List<Backup> values;
    public String DEBUG_TAG = "!!BLAD";
    static final int PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE = 1;
    private String fileToDelete;
    DatabaseHandler db;
    private int rowId, numTimers;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        private TextView txtHeader;
        private TextView txtFooter;
        private TextView contextMenu;
        public View layout;

        private ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = v.findViewById(R.id.backupListLine1);
            txtFooter = v.findViewById(R.id.backupListLine2);
            contextMenu = v.findViewById(R.id.backupRowOptions);
        }
    }

    public void add(int position, Backup backup) {
        values.add(position, backup);
        notifyItemInserted(position);
    }

    private void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BackupListAdapter(List<Backup> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public BackupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.bachup_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Backup backup = values.get(position);
        // remove the.db extension when displaying the file name
        String filenameWithoutExtension = backup.getFilename().substring(0, backup.getFilename().lastIndexOf('.'));
        holder.txtHeader.setText(filenameWithoutExtension);
        //holder.txtHeader.setOnClickListener(v -> remove(position));
        holder.txtFooter.setText(backup.getNotes());

        holder.contextMenu.setOnClickListener(view -> {

            //*************** highlight the clicked row *******************//

            //creating a popup menu
            Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.MaterialBaseBaseTheme_Light);
            PopupMenu popup = new PopupMenu(wrapper, view);
            //PopupMenu popup = new PopupMenu(view.getContext(), holder.contextMenu);
            //inflating menu from xml resource
            popup.inflate(R.menu.backup_options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.backupMenuItemDelete:
                        //Log.d(DEBUG_TAG,"getAdapterPosition is " + holder.getAdapterPosition());
                        int positionToDelete = holder.getAdapterPosition();
                        fileToDelete = values.get(positionToDelete).getFilename();
                        rowId = values.get(positionToDelete).getKey();
                        numTimers = values.get(positionToDelete).getNumrows();
                        //Log.d(DEBUG_TAG,"Filename is " + fileToDelete);
                        show_delete_dialog(view.getContext(), filenameWithoutExtension, positionToDelete);
                        //handle menu1 click
                        break;
                    case R.id.backupMenuItemRestore:
                        //Log.d(DEBUG_TAG,"Filename is " + values.get(position).getFilename());

                        //handle menu2 click
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    private void show_delete_dialog(Context context, String fileToDelete, int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.delete_backup_title)
                .content(R.string.delete_backup_message,fileToDelete,numTimers)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive((dlg,which) -> {
                    delete_backup_from_fs(context);
                    delete_backup_from_db(context);
                    remove(position);
                    //mCallback.OnBackupDeleted(position);
                    // Toast.makeText(context, "Clicked OK", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void delete_backup_from_fs(Context c) {
        String[] perms = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(c, perms)) {
            //Log.d(DEBUG_TAG,"HAS PERMISSION to delete");
            delete_backup_file();

            // Have permissions, do the thing!
            //Toast.makeText(c, "Permission ok", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(DEBUG_TAG,"ASK FOR PERMISSION");

            // Ask for permission
            String appName = String.format(c.getString(R.string.rationale_external_storage),
                    c.getApplicationInfo().loadLabel(c.getPackageManager()).toString());
            EasyPermissions.requestPermissions((Activity) c, appName,
                    PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {

        delete_backup_file();

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        Log.d(DEBUG_TAG,"The user denied permission");
    }

    private void delete_backup_file() {

        //Log.d(DEBUG_TAG,"delting file..");
        String appBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407/backups"; // Hopefully no one else is using this
        File file = new File(appBasePath, fileToDelete);
        boolean deleted = file.delete();
        //Log.d(DEBUG_TAG,"result of delete is " + deleted);

    }

    private void delete_backup_from_db(Context context) {

        // Now delete the entry from the database
        db = new DatabaseHandler(context, null, null, 1);
        db.deleteBackupRow(rowId);

    }



}
