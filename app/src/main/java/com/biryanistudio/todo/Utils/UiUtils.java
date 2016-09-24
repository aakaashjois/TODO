package com.biryanistudio.todo.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		View rootView = (( Activity ) context).getWindow()
				.getDecorView().findViewById(android.R.id.content);
		View toastLayout = LayoutInflater.from(context)
				.inflate(R.layout.toast_message,
						( ViewGroup ) rootView.findViewById(R.id.toast_layout));
		TextView toastMessage = ( TextView ) toastLayout.findViewById(R.id.toast_message_text);
		toastMessage.setText(message);
		toast.setView(toastLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		return toast;
	}
}
