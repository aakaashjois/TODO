package com.biryanistudio.todo.Utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.biryanistudio.todo.R;

/**
 * Created by Aakaash Jois on 24-09-2016 at 06:10 PM.
 */

public class UiUtils {

	public static Toast createToast(Context context, String message) {
		Toast toast = new Toast(context);
		//TODO: Cannot get custom toasts to work from Service
		LayoutInflater inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastRoot = inflater.inflate(R.layout.toast_message, null, false);
		TextView toastMessage = ( TextView ) toastRoot.findViewById(R.id.toast_message_text);
		toastMessage.setText(message);
		toast.setView(toastRoot);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		return toast;
	}
}
