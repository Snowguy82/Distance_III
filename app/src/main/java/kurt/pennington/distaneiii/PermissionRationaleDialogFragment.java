package kurt.pennington.distaneiii;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


/**
 * ${Project_Name} Created by Kurt 05 2016
 * <p/>
 * Software Development for Mobile Devices Metropolitan State University of Denver
 * <p/>
 */

public class PermissionRationaleDialogFragment extends DialogFragment {

	public static final int FINE = 0;
	public static final int COARSE = 1;


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int dialogInt = savedInstanceState.getInt("Location Permission");
		int messageInt;

		switch (dialogInt) {
			case FINE:
				messageInt = R.string.fine_location_rationale;
				break;
			case COARSE:
				messageInt = R.string.coarse_location_rationale;
				break;
			default:
				messageInt = R.string.default_message;
		}

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		alertBuilder.setMessage(messageInt);

		return alertBuilder.create();
	}

}

