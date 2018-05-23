package cn.lt.game.ui.app.rank;

import cn.lt.game.ui.app.rank.RankFragment.SelectorTabListener;
import cn.lt.game.ui.app.rank.supers.AbstractFragmentPageBuilder;

public class RankFragmentPageBuilder extends AbstractFragmentPageBuilder<RankFragment>{
	
	private static RankFragmentPageBuilder instance = new RankFragmentPageBuilder();
	private SelectorTabListener selectorListener;
	
	private RankFragmentPageBuilder(){}

    public static RankFragmentPageBuilder instance() {
		return instance;
	}
	
	@Override
	protected RankFragment newFragment(String type) {
		RankFragment rankFragment = RankFragment.newInstance(type);
		rankFragment.setSelectorTabListener(selectorListener);
		return rankFragment;
	}

	@Override
	protected String[] getPageTypes() {
		return RankTabInfoProvide.instance().getPageTypes();
	}
	
	public void setSelectorTabListener(SelectorTabListener selectorListener) {
		this.selectorListener = selectorListener;
	}

}
