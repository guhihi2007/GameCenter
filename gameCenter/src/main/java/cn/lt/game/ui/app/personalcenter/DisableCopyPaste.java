package cn.lt.game.ui.app.personalcenter;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class DisableCopyPaste {
	
	public static void disable(EditText et) {
		
		int sdkVerInt = Build.VERSION.SDK_INT;
		
		if (sdkVerInt > Build.VERSION_CODES.HONEYCOMB) {
			et.setLongClickable(false);
			
		} else {
			et.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}
			});
		}
	}
}
