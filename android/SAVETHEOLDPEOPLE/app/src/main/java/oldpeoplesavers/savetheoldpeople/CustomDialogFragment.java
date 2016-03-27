package oldpeoplesavers.savetheoldpeople;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.dd.morphingbutton.MorphingButton;
import com.firebase.client.Firebase;

/**
 * Created by Tony on 16-03-27.
 */
public class CustomDialogFragment extends DialogFragment {


    Firebase memoref;
    MorphingButton addBtn;

    public CustomDialogFragment(){
        memoref = new Firebase("https://watchdog-app.firebaseio.com/Bill/People/Andrew/memos");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View subView = inflater.inflate(R.layout.addlay, null);

        final EditText titleE = (EditText)subView.findViewById(R.id.titleEdit);
        final EditText contentE = (EditText)subView.findViewById(R.id.contentEdit);
        addBtn = (MorphingButton)getActivity().findViewById(R.id.addBtn);

        builder.setTitle("Create Memo");
        builder.setView(subView);


        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = titleE.getText().toString();
                String content = contentE.getText().toString();

                Memo memo = new Memo(title,content,MainActivity.getCurrentTime());

                memoref.push().setValue(memo);
                MorphingButton.Params circle = MorphingButton.Params.create()
                        .duration(500)
                        .cornerRadius(256) // 56 dp// 56 dp
                        .width(256)
                        .height(256)
                        .color(Color.parseColor("#BBDEFB")) // normal state color
                        .icon(R.drawable.ic_done_white_48dp); // icon
                addBtn.morph(circle);

                addBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MorphingButton.Params rekt = MorphingButton.Params.create()
                                .duration(500)
                                .width(670)
                                .height(150)
                                .color(Color.parseColor("#F44336"))
                                .text("Add Memo");
                        addBtn.morph(rekt);
                    }
                }, 2000);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
