package JAG.ca.mcmahon.custom.nodes;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField
{

	String numberRegEx =   "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
	
	@Override
    public void replaceText(int start, int end, String text) 
	{
        String oldValue = getText();
        if ((validate(text))) {
            super.replaceText(start, end, text);
            String newText = super.getText();
            if (!validate(newText)) {
                super.setText(oldValue);
            }
        }
    }
	
	@Override
    public void replaceSelection(String text) 
	{
        String oldValue = getText();
        if (validate(text)) {
            super.replaceSelection(text);
            String newText = super.getText();
            if (!validate(newText)) {
                super.setText(oldValue);
            }
        }
    }

    private boolean validate(String text) {
        return (text.equals("") || text.matches(numberRegEx));
    }

}
