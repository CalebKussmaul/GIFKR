package gui;

import java.awt.event.ActionListener;

public interface ProgressDisplay {

	void setProgress(double d, String text);
	
	void showProgress();
	void setCancel(ActionListener onCancel);
	void hideProgress();
}
