package cn.lt.game.ui.installbutton;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.lt.game.model.GameBaseDetail;

public class ManageUpgradeButton extends ManageInstallButton{

	public ManageUpgradeButton(GameBaseDetail game, Button btn,
							   ProgressBar pb, TextView traffic, ImageView networkIndication, TextView managementDownSize) {
		
		super(game, btn, pb, traffic,networkIndication,managementDownSize);
	}
	
	@Override
	protected void onIgnoreUpgrade() {
		onUpgrade();
	}

}
