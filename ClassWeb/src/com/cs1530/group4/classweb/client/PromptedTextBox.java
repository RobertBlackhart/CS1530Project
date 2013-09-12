package com.cs1530.group4.classweb.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

public class PromptedTextBox extends TextBox implements KeyPressHandler, FocusHandler, BlurHandler, ClickHandler
{
	private String promptText;
	private String promptStyle;

	public PromptedTextBox(String promptText, String promptStyleName)
	{
		this.promptText = promptText;
		this.promptStyle = promptStyleName;
		this.addKeyPressHandler(this);
		this.addFocusHandler(this);
		this.addClickHandler(this);
		this.addBlurHandler(this);
		showPrompt();
	}

	public void showPrompt()
	{
		this.addStyleName(promptStyle);
		this.setText(this.promptText);
	}

	public void hidePrompt()
	{
		this.setText(null);
		this.removeStyleName(promptStyle);
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(promptText.equals(this.getText()) && !(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB))
		{
			hidePrompt();
		}
	}

	@Override
	public void onFocus(FocusEvent event)
	{
		this.setCursorPos(0);
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if(promptText.equals(this.getText()))
			hidePrompt();
	}

	@Override
	public void onBlur(BlurEvent event)
	{
		if(getText().length() == 0)
			showPrompt();
	}
}