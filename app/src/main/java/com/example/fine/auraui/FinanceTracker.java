package com.example.fine.auraui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FINE on 4/13/2017.
 */

public class FinanceTracker extends Activity{

public TextView expense;


protected void showInputDialog() {
    LayoutInflater layoutInflater = LayoutInflater.from(FinanceTracker.this);
    View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FinanceTracker.this);
    alertDialogBuilder.setView(promptView);

    final EditText editText = (EditText) promptView.findViewById(R.id.editText_Expense);

    alertDialogBuilder.setCancelable(false)
            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    expense.setText(editText.getText());
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

    AlertDialog alert = alertDialogBuilder.create();
    alert.show();
}//end showInputDialog

    protected void Write_to_File(String sBody)
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Expenses");
            // if external memory exists and folder with name Expenses
            if (!root.exists())
            {
                root.mkdirs(); // this will create folder.
            }
            File file = new File(root, "expenses.txt");  // file path to save

            FileWriter writer=new FileWriter(file,true);
            writer.append(currentDate+","+sBody);
            writer.append(System.getProperty("line.separator"));
            writer.flush();
            writer.close();
        }//end try
        catch(IOException e)
        {
         e.printStackTrace();
        }//end catch
    }//end Write_to_File
}//end FinanceTracker
