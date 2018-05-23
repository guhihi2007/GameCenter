package cn.lt.game.ui.app.rank;

import cn.lt.game.R;
import cn.lt.game.ui.app.rank.supers.AbstractTabInfoProvide;

public class RankTabInfoProvide extends AbstractTabInfoProvide{
	
	
	private static RankTabInfoProvide instance = new RankTabInfoProvide();
	
	private RankTabInfoProvide(){}
	
	public static RankTabInfoProvide instance() {
		return instance;
	}
	
	@Override
	protected int[] makeTitles() {
		return new int[] { R.string.single_game, R.string.online_game, R.string.hottest, R.string.newest };
	}

	@Override
	protected String[] makePageTypes() {
		return new String[] {"offline", "online", "hot", "new"};
	}
}
